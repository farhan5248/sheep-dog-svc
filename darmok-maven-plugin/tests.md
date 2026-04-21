# Darmok path enumeration

Working document for #253. Derived from reading the darmok-maven-plugin source at svc commit `1653d1c`:

- `DarmokMojo.java` — shared flow (init → runCleanUp → process scenarios → cleanup) and all phase logic
- `GenFromExistingMojo.java` — goal `gen-from-existing`
- `GenFromComparisonMojo.java` — goal `gen-from-comparison`
- `ClaudeRunner.java`, `GitRunner.java`, `MavenRunner.java`, `ProcessRunner.java` — orchestrated subprocesses

---

## Two goals

| Goal | Project | Key difference |
|---|---|---|
| `darmok:gen-from-existing` | `darmok-maven-plugin` | plain loop over scenarios-list |
| `darmok:gen-from-comparison` | `darmok-maven-plugin` | runs claude `/rgr-gen-from-comparison` before each scenario iteration |

Both share the rest of the flow.

---

## Process tree

```
execute(goal)
├── init() — sets up two CategoryLog files under target/darmok/ (or $LOG_PATH)
│              darmok.mojo.<date>.log   (mojo-level orchestration lines)
│              darmok.runners.<date>.log (subprocess command + stdout capture)
│          — verifies the `gitBranch` parameter against the git HEAD
│              (all failures log ERROR to mojo.log before throwing MojoExecutionException,
│               so the log line is the observable contract; the exception message matches it verbatim)
│              ├── gitBranch param unset/empty       → log ERROR "gitBranch parameter is required", abort
│              ├── git rev-parse --abbrev-ref HEAD   → actualBranch
│              │     └── detached HEAD returns "HEAD" (treated as mismatch)
│              ├── gitBranch != actualBranch         → log ERROR "Darmok configured for branch '<gitBranch>'
│              │                                             but current HEAD is on '<actualBranch>'. Aborting.", abort
│              └── gitBranch == actualBranch         → proceed; value flows to the git_branch column on every metrics row
├── runCleanUp()
│   ├── deleteNulFiles(../..)           → logs count
│   └── deleteDirectory(target/)        → logs "Deleted target directory"
│   └── if exit != 0 → FAIL "Clean up failed with exit code N"
├── loop:
│   (for gen-from-comparison only)
│   └── claude /rgr-gen-from-comparison <artifactId>
│        └── if non-zero → FAIL "rgr-gen-from-comparison failed with exit code N"
│
│   entry = getNextScenario()
│   ├── scenarios-list.txt missing or empty → null → exit loop
│   └── else → processScenario(entry)
│        ├── if entry.tag == "NoTag"
│        │     └── log "Skipping (NoTag)", removeFirstScenarioFromFile, return  (no commit)
│        ├── else:
│        │   ├── addTagToAsciidoc(file, scenario, tag)
│        │   │     ├── asciidoc file missing          → warn "File not found"
│        │   │     ├── target tag already present     → log "Tag @X already present in file"  (no file change)
│        │   │     ├── existing tag line, other tags  → append "@X" to line, log "Added tag @X"
│        │   │     └── no tag line under Test-Case    → insert "@X" line, log "Added tag @X"
│        │   ├── log "Red: Running maven..."
│        │   ├── runRgrRed(tag)
│        │   │     ├── mvn asciidoctor-to-uml
│        │   │     ├── mvn uml-to-cucumber-guice
│        │   │     ├── write src/test/java/.../suites/<tag>Test.java runner
│        │   │     └── mvn test -Dtest=<tag>Test
│        │   │          ├── tests pass (exit 0)   → return 100  (signal: nothing to do)
│        │   │          └── tests fail            → return 0    (proceed to green)
│        │   ├── log "Red: Completed maven (HH:MM:SS)"
│        │   ├── if redExit not in {0, 100} → FAIL "rgr-red failed with exit code N"
│        │   ├── if redExit == 100:  (tests already passing)
│        │   │     ├── log "Green: Skipped (tests already passing)"
│        │   │     ├── removeFirstScenarioFromFile
│        │   │     ├── git add .
│        │   │     └── commitIfChanged "run-rgr red <scenario>"
│        │   └── else: (tests failing — full RGR)
│        │         ├── git add .
│        │         ├── if !stage: commitIfChanged "run-rgr red <scenario>"
│        │         ├── log "Green: Running..."
│        │         ├── runRgrGreen → claude /rgr-green <artifactId> <tag>
│        │         │     ├── exit 0                         → proceed
│        │         │     ├── retryable 500/529/overloaded   → retry up to maxRetries
│        │         │     │     ├── recovers                 → proceed (WARN markers in log)
│        │         │     │     └── exhausted                → return non-zero (ERROR markers)
│        │         │     └── non-retryable non-zero         → return non-zero (single attempt)
│        │         ├── if greenExit != 0 → FAIL "rgr-green failed with exit code N"
│        │         ├── log "Green: Completed (HH:MM:SS)"
│        │         ├── removeFirstScenarioFromFile
│        │         ├── git add .
│        │         ├── if !stage: commitIfChanged "run-rgr green <scenario>"
│        │         ├── log "Refactor: Running..."
│        │         ├── runRgrRefactor → claude /rgr-refactor <pipeline> <artifactId>
│        │         │     └── (same retry semantics as green)
│        │         ├── if refactorExit != 0 → FAIL "rgr-refactor failed with exit code N"
│        │         ├── log "Refactor: Completed (HH:MM:SS)"
│        │         ├── git add .
│        │         └── commitIfChanged  (message depends on stage flag)
│        │              ├── stage == false → "run-rgr refactor <scenario>"
│        │              └── stage == true  → "run-rgr <scenario>"   (single combined commit)
│        └── log METRIC lines (red-maven / green / refactor / total durations)
├── log "RGR Automation Complete! Total scenarios processed: N"
└── cleanup() — closes both CategoryLog files
```

---

## Input dimensions

Parameters that change observable behavior:

| Dimension | Values |
|---|---|
| `gitBranch` parameter | unset · matches current HEAD · mismatches current HEAD · detached HEAD |
| `scenariosFile` (state of scenarios-list.txt) | absent · empty · has N entries |
| `scenario.tag` | `NoTag` · regular tag |
| asciidoc file state (per entry) | missing · target tag present · other tag line present · no tag line |
| red phase outcome | tests pass (→100) · tests fail (→0) |
| green phase outcome | success · non-retryable fail · retryable-recover · retryable-exhaust |
| refactor phase outcome (only if green succeeded) | success · fail |
| `stage` parameter | `true` (one combined commit) · `false` (one commit per phase) |
| `pipeline` parameter | `forward` · `reverse` |
| `onlyChanges` | `true` · `false` (used by svc-plugin goals) |
| `LOG_PATH` env | unset (target/darmok/) · set (use that dir) |

Observably distinct paths grouped below.

---

## Paths

Each path is one scenario processed (unless labeled "no-scenarios" or "comparison"). Some pre-processing paths (cleanup, addTag variations) apply to every run. They're listed once at the top; scenario-processing paths assume they've already executed successfully.

### Preamble: branch verification (every run, fail-fast before any work)

Each Darmok run must declare which branch it was configured for. The branch name
becomes the `git_branch` column on every row of `metrics.csv` so the SPC dashboard
can colour and filter points by run. The init-time check also prevents a
misconfigured Darmok from silently producing commits on the wrong branch.

Order in `init()` is: resolve baseDir → open logs → verify gitBranch → return. The
branch check fires before `runCleanUp`, so on failure no subprocess runs and no
`target/` state is touched.

#### Path BV-1 — gitBranch parameter is unset → abort

- Given the `gitBranch` parameter is not supplied (no `-DgitBranch=...`, no `<gitBranch>` in the POM)
- When the `darmok:gen-from-existing` goal is executed
- Then the darmok-prj project `target/darmok/darmok.mojo.<date>.log` file contains
  ```
  | Level | Category | Content                         |
  | ERROR | mojo     | gitBranch parameter is required |
  ```
- And the darmok-prj project `target/darmok/darmok.runners.<date>.log` file is Empty
  (no `git rev-parse --abbrev-ref HEAD` was called — the null-check short-circuits first)
- And the darmok-prj project `metrics.csv` file is Absent (run never reached processScenario)

#### Path BV-2 — gitBranch mismatches the current HEAD → abort

- Given the `gitBranch` parameter is `Rebuild30`
- And `git rev-parse --abbrev-ref HEAD` returns `main`
- When the `darmok:gen-from-existing` goal is executed
- Then the darmok-prj project `target/darmok/darmok.mojo.<date>.log` file contains
  ```
  | Level | Category | Content                                                                           |
  | ERROR | mojo     | Darmok configured for branch 'Rebuild30' but current HEAD is on 'main'. Aborting. |
  ```
- And the darmok-prj project `target/darmok/darmok.runners.<date>.log` file contains
  exactly one line:
  ```
  | Level | Category | Content                                  |
  | DEBUG | runner   | Running: git rev-parse --abbrev-ref HEAD |
  ```
- And the darmok-prj project `metrics.csv` file is Absent

#### Path BV-3 — detached HEAD → abort

- Given the `gitBranch` parameter is `Rebuild30`
- And `git rev-parse --abbrev-ref HEAD` returns the literal string `HEAD` (detached state)
- When the `darmok:gen-from-existing` goal is executed
- Then the mojo log contains the same mismatch ERROR line as BV-2 with `actualBranch=HEAD`
- (Detached HEAD is not a separate code path; it collapses into the `gitBranch != actualBranch`
  branch of the check. Documented as its own row because it's the failure mode users hit
  most often when running Darmok from a sub-repository in the middle of a rebase.)

#### Path BV-4 — gitBranch matches current HEAD → proceed

- Given the `gitBranch` parameter is `Rebuild30`
- And `git rev-parse --abbrev-ref HEAD` returns `Rebuild30`
- When the `darmok:gen-from-existing` goal is executed
- Then init returns cleanly, control passes to `runCleanUp` (see Path 0a / 0b)
- And every row subsequently appended to `metrics.csv` has `git_branch=Rebuild30`

---

### Preamble: init + runCleanUp (every run)

#### Path 0a — Fresh checkout, nothing to clean

- Given the darmok-prj project has no `target/` directory
- And the darmok-prj project has no NUL-named files in its parent tree
- When the `darmok:gen-from-existing` goal is executed
- Then the darmok-prj project `target/darmok/darmok.mojo.<date>.log` file is Present
- And the darmok-prj project `target/darmok/darmok.mojo.<date>.log` file contains
  ```
  | Level | Category | Content                              |
  | INFO  | mojo     | RGR Automation Plugin (...)          |
  | DEBUG | mojo     | Cleanup: Deleted 0 NUL files         |
  | DEBUG | mojo     | Cleanup: Deleted target directory    |
  ```

#### Path 0b — Prior run artifacts present

- Given the darmok-prj project `target/` directory is Present with nested build output
- And the darmok-prj project has N NUL-named files in its parent tree
- When the `darmok:gen-from-existing` goal is executed
- Then the darmok-prj project `target/` directory is Absent (deleted then re-created for logs)
- And the darmok-prj project `target/darmok/darmok.mojo.<date>.log` file contains
  ```
  | Level | Category | Content                              |
  | DEBUG | mojo     | Cleanup: Deleted N NUL files         |
  | DEBUG | mojo     | Cleanup: Deleted target directory    |
  ```

---

### No-work paths (zero scenarios processed)

#### Path 1 — scenarios-list.txt is absent

- Given the darmok-prj project `scenarios-list.txt` file is Absent
- When the `darmok:gen-from-existing` goal is executed
- Then the darmok-prj project `target/darmok/darmok.mojo.<date>.log` file contains
  ```
  | Level | Category | Content                                       |
  | INFO  | mojo     | RGR Automation Complete!                      |
  | INFO  | mojo     | Total scenarios processed: 0                  |
  ```
- And the darmok-prj project `target/darmok/darmok.runners.<date>.log` file is Empty

#### Path 2 — scenarios-list.txt is Empty

- Given the darmok-prj project `scenarios-list.txt` file is Empty
- When the `darmok:gen-from-existing` goal is executed
- Then the observable outcome is identical to Path 1

#### Path 3 — scenarios-list entry has tag "NoTag"

- Given the darmok-prj project `scenarios-list.txt` file contains one entry with tag `NoTag`
- When the `darmok:gen-from-existing` goal is executed
- Then the darmok-prj project `scenarios-list.txt` file is Empty
- And the darmok-prj project `target/darmok/darmok.mojo.<date>.log` file contains
  ```
  | Level | Category | Content                              |
  | INFO  | mojo     | Processing Scenario: ... [NoTag]     |
  | INFO  | mojo     |   Skipping (NoTag)                   |
  ```
- And the darmok-prj project `target/darmok/darmok.mojo.<date>.log` file will not contain
  ```
  | Content                |
  |   Red: Running maven...|
  |   Green: Running...    |
  |   Refactor: Running... |
  |   Red: Committing      |
  ```
- And the darmok-prj project `target/darmok/darmok.runners.<date>.log` file is Empty

---

### Tag-insertion variations (applies inside scenario processing)

These four paths differ only in the asciidoc-file state prior to Darmok touching it. They all flow into one of the phase paths below.

#### Path 4 — asciidoc file is Absent

- Given the darmok-prj project `src/test/resources/asciidoc/specs/<file>.asciidoc` file is Absent
- And the darmok-prj project `scenarios-list.txt` contains one entry pointing at that file with a regular tag
- When the `darmok:gen-from-existing` goal is executed
- Then the darmok-prj project `target/darmok/darmok.mojo.<date>.log` file contains
  ```
  | Level | Category | Content                              |
  | WARN  | mojo     | File not found: <file>.asciidoc      |
  ```
- And processing continues (flow enters the red phase — see Path 5+ for outcomes)

#### Path 5 — asciidoc already has the target tag

- Given the asciidoc file already has `@<targetTag>` under its `Test-Case`
- When the `darmok:gen-from-existing` goal is executed
- Then the asciidoc file is unchanged
- And the log contains `DEBUG [mojo]   Tag @<targetTag> already present in file`
- And processing continues

#### Path 6 — asciidoc has a tag line with different tags

- Given the asciidoc file's `Test-Case` already has `@someOtherTag`
- When the `darmok:gen-from-existing` goal is executed
- Then the asciidoc file now has `@someOtherTag @<targetTag>` on that line
- And the log contains `DEBUG [mojo]   Added tag @<targetTag> to file`

#### Path 7 — asciidoc has no tag line

- Given the asciidoc file's `Test-Case` has no tag line
- When the `darmok:gen-from-existing` goal is executed
- Then the asciidoc file now has a `@<targetTag>` line below the `Test-Case` header
- And the log contains `DEBUG [mojo]   Added tag @<targetTag> to file`

---

### Phase-outcome paths (the substantive RGR paths)

All assume: exactly one scenario in the list with a regular tag; the asciidoc file exists and ends up with the tag applied (per any of Paths 5–7).

#### Path 8 — Red phase: tests already passing

- Given the darmok-prj project has src/main/java already containing the implementation for `<targetTag>`
- And the darmok-prj project `scenarios-list.txt` contains one entry with a regular tag
- When the `darmok:gen-from-existing` goal is executed
- Then the darmok-prj project `scenarios-list.txt` file is Empty
- And the darmok-prj project `src/main/java/.../<targetTag>.java` file is Present (unchanged)
- And the mvn command was executed with
  ```
  | Args                                                              |
  | org.farhan:sheep-dog-svc-maven-plugin:asciidoctor-to-uml ...      |
  | org.farhan:sheep-dog-svc-maven-plugin:uml-to-cucumber-guice ...   |
  | test -Dtest=<targetTag>Test                                       |
  ```
- And the git command was executed with `add .` followed by `commit -m "run-rgr red <scenario>\n\nCo-Authored-By: ..."`
- And the darmok-prj project `target/darmok/darmok.mojo.<date>.log` file contains
  ```
  | Level | Category | Content                                  |
  | INFO  | mojo     |   Red: Running maven...                  |
  | INFO  | mojo     |   Red: Completed maven (<any>)           |
  | INFO  | mojo     |   Green: Skipped (tests already passing) |
  | INFO  | mojo     |   Red: Committing                        |
  ```
- And the darmok-prj project `target/darmok/darmok.mojo.<date>.log` file will not contain
  ```
  | Content                |
  |   Green: Running...    |
  |   Refactor: Running... |
  ```

#### Path 9 — Red fails, green succeeds, refactor succeeds, `stage=false`

- Given the darmok-prj project `src/main/java/.../<targetTag>.java` file is Absent
- And the darmok-prj project `scenarios-list.txt` contains one entry with a regular tag
- When the `darmok:gen-from-existing` goal is executed with `| stage | false |`
- Then the darmok-prj project `src/main/java/.../<targetTag>.java` file is Present
- And the darmok-prj project `scenarios-list.txt` file is Empty
- And the mvn command was executed with the red-phase goals and `test` (exit non-zero first time)
- And the claude command was executed with
  ```
  | Args                                         |
  | /rgr-green <artifactId> <targetTag>          |
  | /rgr-refactor <pipeline> <artifactId>        |
  ```
- And the git command was executed with `commit -m "run-rgr red <scenario>..."`, `commit -m "run-rgr green <scenario>..."`, `commit -m "run-rgr refactor <scenario>..."` — three separate commits
- And the log contains the full phase-sequence markers:
  ```
  | Level | Category | Content                                 |
  | INFO  | mojo     |   Red: Running maven...                 |
  | INFO  | mojo     |   Red: Completed maven (<any>)          |
  | INFO  | mojo     |   Red: Committing                       |
  | INFO  | mojo     |   Green: Running...                     |
  | INFO  | mojo     |   Green: Completed (<any>)              |
  | INFO  | mojo     |   Green: Committing                     |
  | INFO  | mojo     |   Refactor: Running...                  |
  | INFO  | mojo     |   Refactor: Completed (<any>)           |
  | INFO  | mojo     |   Refactor: Committing                  |
  | INFO  | mojo     |   METRIC\|scenario=...\|phase=total\|... |
  ```

#### Path 10 — Red fails, green succeeds, refactor succeeds, `stage=true`

- Given the same inputs as Path 9
- When the `darmok:gen-from-existing` goal is executed with `| stage | true |`
- Then the observable outcome matches Path 9 except
  - the git command was executed with exactly one `commit -m "run-rgr <scenario>..."`
  - the mojo log has no `Red: Committing` / `Green: Committing` lines (only `Refactor: Committing`)

#### Path 11 — Green phase: claude fails non-retryably

- Given the darmok-prj project `src/main/java/.../<targetTag>.java` file is Absent
- And the claude command, when executed with `/rgr-green ...`, will return a non-retryable exit code
- When the `darmok:gen-from-existing` goal is executed with `| stage | false |`
- Then the goal fails with `MojoExecutionException`: `rgr-green failed with exit code <N>`
- And the git command was executed with `commit -m "run-rgr red <scenario>..."` (red was already committed)
- And the darmok-prj project `src/main/java/.../<targetTag>.java` file is Absent (green never succeeded)
- And the darmok-prj project `target/darmok/darmok.mojo.<date>.log` file will not contain
  ```
  | Content                     |
  |   Green: Completed          |
  |   Green: Committing         |
  |   Refactor: Running...      |
  ```
- And the darmok-prj project `target/darmok/darmok.runners.<date>.log` file contains
  ```
  | Level | Category | Content                                 |
  | DEBUG | runner   | Claude CLI exited with code <N>         |
  ```
- And the darmok-prj project `target/darmok/darmok.runners.<date>.log` file will not contain
  ```
  | Content                     |
  | Retry attempt               |
  | Retryable error detected    |
  | Executing: claude ... /rgr-refactor ... |
  ```

#### Path 12 — Green phase: claude retries and recovers

- Given the claude upstream for `/rgr-green ...` returns exit 1 with `API Error: 500` on first call, then exit 0
- When the `darmok:gen-from-existing` goal is executed
- Then the observable outcome matches Path 9 (green eventually succeeds)
- And the runner log additionally contains
  ```
  | Level | Category | Content                                            |
  | WARN  | Claude   | Retryable error detected: API Error: 500           |
  | WARN  | Claude   | Waiting <N> seconds before retry...                |
  | DEBUG | Claude   | Retry attempt 2 of 3                               |
  | DEBUG | Claude   | Claude CLI completed successfully                  |
  ```

#### Path 13 — Green phase: retries exhausted

- Given the claude upstream for `/rgr-green ...` returns exit 1 with `API Error: 500` on every call
- When the `darmok:gen-from-existing` goal is executed
- Then the goal fails with `MojoExecutionException`: `rgr-green failed with exit code 1`
- And the darmok-prj project `target/darmok/darmok.runners.<date>.log` file contains at ERROR level
  ```
  | Level | Category | Content                                   |
  | ERROR | runner   | Retryable error detected: API Error: 500  |
  | ERROR | runner   | Max retries (<maxRetries>) exhausted      |
  ```
- And the darmok-prj project `target/darmok/darmok.runners.<date>.log` file will not contain
  ```
  | Content                                 |
  | Executing: claude ... /rgr-refactor ... |
  ```

#### Path 14 — Green succeeds, refactor fails

- Given the darmok-prj project `src/main/java/.../<targetTag>.java` file is Absent
- And the claude command for `/rgr-refactor ...` will return a non-zero exit code
- When the `darmok:gen-from-existing` goal is executed with `| stage | false |`
- Then the goal fails with `MojoExecutionException`: `rgr-refactor failed with exit code <N>`
- And the darmok-prj project `src/main/java/.../<targetTag>.java` file is Present (green succeeded)
- And the git command was executed with `commit -m "run-rgr red <scenario>..."` and `commit -m "run-rgr green <scenario>..."`
- And the darmok-prj project `target/darmok/darmok.mojo.<date>.log` file will not contain
  ```
  | Content                     |
  |   Refactor: Completed       |
  |   Refactor: Committing      |
  ```

---

### gen-from-comparison specific

The comparison goal runs a `/rgr-gen-from-comparison` skill before each scenario-processing iteration.

#### Path 15 — Comparison skill succeeds, then standard paths apply

- Given the darmok-prj project `scenarios-list.txt` contains one entry with a regular tag
- When the `darmok:gen-from-comparison` goal is executed
- Then the claude command was executed with
  ```
  | Args                                     |
  | /rgr-gen-from-comparison <artifactId>    |
  ```
- Before the claude command was executed with `/rgr-green ...` and `/rgr-refactor ...`
- And the remaining observable outcome matches whichever phase path applies (8–14)

#### Path 16 — Comparison skill fails

- Given the claude upstream for `/rgr-gen-from-comparison` will return a non-zero exit code
- When the `darmok:gen-from-comparison` goal is executed
- Then the goal fails with `MojoExecutionException`: `rgr-gen-from-comparison failed with exit code <N>`
- And the darmok-prj project `target/darmok/darmok.mojo.<date>.log` file will not contain
  ```
  | Content                             |
  | Processing Scenario:                |
  |   Red: Running maven...             |
  |   Green: Running...                 |
  |   Refactor: Running...              |
  ```

---

### runCleanUp failure

#### Path 17 — Cleanup fails

- Given a file under `target/` is locked such that `deleteDirectory` would throw
- When the `darmok:gen-from-existing` goal is executed
- Then the goal fails with `MojoExecutionException`: `Clean up failed with exit code <N>`
- And the darmok-prj project `target/darmok/darmok.mojo.<date>.log` file will not contain
  ```
  | Content                             |
  | Processing Scenario:                |
  | RGR Automation Complete!            |
  ```

*(Note: the current implementation logs a per-file warning and keeps going; `runCleanUp` always returns 0 in the current code. This path exists in the exception-handling branch of the goal; whether it's reachable is a Stage 2 investigation.)*

---

## Observations while enumerating

1. **Path 0a and 0b are invariants** — every run goes through them. Could be expressed as a `Test-Setup` shared across all Test-Cases rather than duplicated.
2. **Paths 4–7 (tag variations) are orthogonal** to Paths 8–14 (phase outcomes). A full matrix would be 4×7 = 28 specs. Realistically 4 tag variations × 1 default phase outcome = 4 specs, plus 7 phase variations × 1 default tag-already-present = 7 specs = 11 specs total. The cross-product isn't worth the maintenance.
3. **`commitIfChanged` behavior** — `git diff --cached --quiet` is run before every commit. If nothing is staged, the commit is skipped. That's a nuance the current tests don't cover; it needs its own path (e.g., "green phase produced no changes — no green commit made").
4. **Pipeline parameter** (`forward` / `reverse`) only affects the refactor prompt string. Probably one path per pipeline value suffices; the observable diff is only in `claude command was executed with` args.
5. **Metrics lines** — every successful scenario emits four METRIC lines. These are consumed by the PBC report skill (`pbc-report-plantuml`), so their format is part of the contract.
5a. **git_branch column** — `metrics.csv` gains a `git_branch` column populated with the value of the `gitBranch` parameter. The column is stable for the whole run (every row has the same value) and the branch is validated against git HEAD at init time, so `git_branch` is always the name of the branch that produced the commits recorded alongside it. The SPC dashboard joins on this column to overlay runs.
6. **`LOG_PATH` env var** — if set, logs land elsewhere. A single path covering "LOG_PATH set" is enough; the rest of the behavior is identical.
7. **Refactor-only path missing** — there's no code path that runs refactor without green. The tree shape is Red → (Green → Refactor) or Red alone.

---

## Notes

- Generated 2026-04-16 from code reading. If code changes, regenerate this tree.
- Path labels (0a, 0b, 1–17) are not part of the eventual spec names; they're just anchors for discussion.
- Source material — the existing JUnit tests from svc commit `d5bae20` — is preserved in git. Replace, don't layer.
