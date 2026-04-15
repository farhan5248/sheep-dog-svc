net grammar work goes to that issue, 257

for this issue, we need to refactor the tests as part of stage 1
document in the issue 
1. what the bdd goal is
2. the style that is adopted

then
3. delete unneeded tests
4. refactor existing tests with comments
5. For now, it's acceptable that some tests start with When (not given) since you didn't already specify the conditions but after refactoring and before doing stage 2, let's add the given statements which communicate what file situation would be that would result in the log file showing what it has. For example if we have a Green: Running, then we have to specify that there were some files already there. Green can't run if there were no files. Because this is an outside in, e2e test with no mocking, we can specify which asciidoc files existed, what the log file will say, which java files will exist

# Darmok tests — Given/When/Then drafts

Working document for #253. Each existing JUnit test rendered as a Given/When/Then sequence so the shape can be reviewed and edited before the Stage 1 refactor lands.

Tests that don't fit the BDD shape naturally are called out at the bottom.

---

## ClaudeRunnerCharacterizationTest

### happyPath_logsExecutingCommand_andSuccessMarker

- Given the claude upstream will return exit 0 with output "mocked claude output line"
- When the claude runner is executed with args `/rgr-green sample-project sampleTag`
- Then the runner log file contains the lines: `Executing: claude …`, `Claude CLI completed successfully`
- And the runner log file contains no ERROR-level lines

### retryRecovers_logsRetryableErrorAndSuccess

- Given the claude upstream will return exit 1 with output "API Error: 500 Internal server error" on the first call
- Given the claude upstream will return exit 0 with output "mocked claude output line" on the second call
- When the claude runner is executed with args `/rgr-green project tag`
- Then the runner log file contains the lines: `Retryable error detected: API Error: 500`, `Waiting 0 seconds before retry`, `Retry attempt 2 of 3`, `Claude CLI completed successfully`

### retriesExhausted_logsMaxRetriesExhaustedAtError

- Given the claude upstream will return exit 1 with output "API Error: 500 Internal server error" on every call
- When the claude runner is executed with args `/rgr-green project tag`
- Then the runner log file contains the lines: `Retryable error detected: API Error: 500`, `Max retries (3) exhausted`, `Claude CLI exited with code 1`

### nonRetryableFailure_doesNotRetry_logsExitCode

- Given the claude upstream will return exit 2 with output "permission denied: /foo" on every call
- When the claude runner is executed with args `/rgr-green project tag`
- Then the runner log file contains the line `Claude CLI exited with code 2`
- And the runner log file does not contain any `Retry attempt` or `Retryable error detected` lines
- And the runner log file contains exactly one `Executing: claude …` line

### subprocessStdout_isMirroredToLogInOrder

- Given the claude upstream will return exit 0 with multi-line output `line one of output / line two of output / line three of output`
- When the claude runner is executed with args `/rgr-green project tag`
- Then the runner log file contains the three output lines in the same order they were produced

### gitRunnerThroughSeam_logsRunningCommand

- Given the git upstream will return exit 0 with output "nothing to commit"
- When the git runner is executed with args `status --porcelain`
- Then the runner log file contains the line `Running: git status --porcelain`

---

## CategoryLogTest

### constructor_createsMissingParentDirectories

- Given a log-file path whose parent directories do not exist
- When a CategoryLog is created targeting that path and one INFO message is written
- Then the parent directories now exist
- And the log file exists and contains the line `INFO  [test] hello`

### writesCanonicalTimestampLevelCategoryFormat

- Given a CategoryLog with category "Claude"
- When debug, info, warn, and error messages are written in that order
- Then the log file contains four lines
- And every line matches the pattern `<timestamp> <level> [<category>] <content>`
- And the four lines carry DEBUG, INFO, WARN, ERROR levels respectively

### withThrowable_appendsStackTraceToLogFile

- Given an IOException with message "boom from test"
- When the exception is passed to `warn(content, throwable)` on a CategoryLog
- Then the log file contains the warn-level content line
- And the log file contains the stack trace of the exception

### close_isIdempotent

- Given a CategoryLog that has been closed once
- When close is called a second time
- Then no file-state change is observable

### appendMode_preservesPreviousSessionContent

- Given an existing log file that contains a line written by a prior CategoryLog session
- When a new CategoryLog is opened on the same file and writes a second line
- Then the log file contains both lines in the order they were written

---

## DarmokMojoHelpersTest

### formatDuration_producesZeroPaddedHoursMinutesSeconds

- Given several millisecond durations: 500, 1000, 60000, 3600000, 3723000
- When each is formatted
- Then the results are `00:00:00`, `00:00:01`, `00:01:00`, `01:00:00`, `01:02:03` respectively

### parseScenarios_singleTriple_returnsOneEntry

- Given a scenarios-list file containing one `File / Scenario / Tag` triple
- When the scenarios-list is parsed
- Then the returned list contains exactly one entry matching the triple

### parseScenarios_multipleFilesAndScenarios_associatesCorrectly

- Given a scenarios-list file with two scenarios under file `features/a.asciidoc` and one scenario under file `features/b.asciidoc`
- When the scenarios-list is parsed
- Then the returned list contains three entries with correct file-to-scenario association

### generateRunnerClassContent_includesSuiteAnnotationsAndTag

- Given a pattern "loginHappyPath" and class name "LoginHappyPathTest"
- When a runner class source is generated
- Then the generated source declares package `org.farhan.suites`, imports the Suite annotations, carries `@Suite` and `@IncludeTags("loginHappyPath")`, and declares `public class LoginHappyPathTest`

### writeFileWithLF_writesLFOnly_evenOnWindows

- Given three lines `alpha`, `beta`, `gamma`
- When they are written via `writeFileWithLF`
- Then the on-disk file exists and contains exactly `alpha\nbeta\ngamma\n`
- And the on-disk file contains no CR bytes

### removeFirstScenarioFromFile_onlyEntry_clearsFile

- Given a scenarios-list file containing exactly one `File / Scenario / Tag` entry
- When `removeFirstScenarioFromFile` is run
- Then the scenarios-list file is empty

### removeFirstScenarioFromFile_secondScenarioSameFile_preservesFileHeader

- Given a scenarios-list file with two scenarios under the same File header
- When `removeFirstScenarioFromFile` is run
- Then the scenarios-list file begins with the original File header
- And the scenarios-list file contains the second scenario and its tag
- And the scenarios-list file does not contain the first scenario or its tag

### getNextScenario_missingFile_returnsNull

- Given no scenarios-list file exists at the configured path
- When the next scenario is requested
- Then null is returned

### getNextScenario_emptyFile_returnsNull

- Given an empty scenarios-list file at the configured path
- When the next scenario is requested
- Then null is returned

### getNextScenario_multipleEntries_returnsFirstInOrder

- Given a scenarios-list file with two scenarios under file `features/a.asciidoc`
- When the next scenario is requested
- Then the first scenario in file order is returned

### addTagToAsciidoc_noExistingTagLine_insertsNewTagLine

- Given an asciidoc file whose `Test-Case` for "User logs in successfully" has no tag line
- When the tag `loginHappyPath` is added to that test-case
- Then the asciidoc file contains a new `@loginHappyPath` line below the Test-Case header

### addTagToAsciidoc_existingTagLine_appendsNewTag

- Given an asciidoc file whose Test-Case already carries tag `@existingTag`
- When the tag `loginHappyPath` is added to that test-case
- Then the asciidoc file contains the line `@existingTag @loginHappyPath`

### addTagToAsciidoc_tagAlreadyPresent_isNoOp

- Given an asciidoc file whose Test-Case already carries tag `@loginHappyPath`
- When the tag `loginHappyPath` is added to that test-case
- Then the asciidoc file is unchanged

### deleteNulFiles_removesOnlyNulEntries_andReturnsCount

- Given a directory tree containing files `NUL`, `sub/nul`, and `keep.txt`
- When `deleteNulFiles` is run on the tree root
- Then the file `NUL` does not exist
- And the file `sub/nul` does not exist
- And the file `keep.txt` exists

### deleteNulFiles_noMatches_returnsZero

- Given a directory tree containing no NUL-named files
- When `deleteNulFiles` is run
- Then no files were deleted

### deleteDirectory_removesEntireNestedTree

- Given a directory tree with nested files under `target/classes/` and `target/build.log`
- When `deleteDirectory` is run on `target/`
- Then the `target/` directory does not exist

### deleteDirectory_missingPath_isNoOp

- Given a path that does not exist
- When `deleteDirectory` is run on it
- Then the path still does not exist

---

## RunnerBuildCommandTest

### claudeRunner_withModel_assemblesFullCommandLine

- Given the claude upstream will return exit 0 on invocation
- When the claude runner with model `sonnet` is executed with args `/rgr-green project tag`
- Then the spawned command line is `claude --print --dangerously-skip-permissions --model sonnet "/rgr-green project tag"`

### claudeRunner_emptyModel_omitsModelFlag

- Given the claude upstream will return exit 0 on invocation
- When the claude runner with an empty model is executed with args `/rgr-green project tag`
- Then the spawned command line is `claude --print --dangerously-skip-permissions "/rgr-green project tag"`
- And the spawned command line does not contain `--model`

### mavenRunner_prependsMvnBinary_andPassesArgsVerbatim

- Given the maven upstream will return exit 0 on invocation
- When the maven runner is executed with args `org.farhan:sheep-dog-svc-maven-plugin:asciidoctor-to-uml -Dtags=loginHappyPath -Dhost=dev.sheepdog.io`
- Then the spawned command line begins with `mvn` followed by those args verbatim

### gitRunner_prependsGitBinary_andPassesArgsVerbatim

- Given the git upstream will return exit 0 on invocation
- When the git runner is executed with args `commit -m "run-rgr green someScenario"`
- Then the spawned command line is `git commit -m "run-rgr green someScenario"`

---

## Tests that fit the BDD shape awkwardly

Left for discussion before the refactor decides their fate.

### formatDuration_producesZeroPaddedHoursMinutesSeconds

Pure-math function. Given is "several durations", Then is "several strings". Natural shape is table-driven, not Given/When/Then. Kept above in BDD form for completeness.

### generateRunnerClassContent_includesSuiteAnnotationsAndTag

Pure string-producing function; the Then asserts on substrings of a return value, not on file state. Not really an outside-observable behavior.

### close_isIdempotent

The Then is "nothing happened / no exception". No observable file-state change.

### RunnerBuildCommandTest (all 4)

The Then asserts on the command line that *would* be spawned (the `ProcessBuilder.command()` list). That's call-argument inspection, which by the "not command output, that's too low level" rule is at a different abstraction than the orchestration-level BDD. Still defensible because the argument list IS the outward contract of `buildCommand`.

---

## Notes

- gitignored? — this file lives under `darmok-maven-plugin/tests.md`. Not gitignored; up to the author whether to commit the working doc or delete it once the refactor lands.
- Source: generated 2026-04-15 from the JUnit tests that exist at svc commit `d102ee7`.
