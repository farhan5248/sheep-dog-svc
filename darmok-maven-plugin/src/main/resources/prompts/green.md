# Green Phase Prompt

You are running the Green phase of a TDD cycle for a darmok-driven scenario. A test is failing in `${runnerClassName}`; your job is to make it pass by editing only the production code under the allowlist.

## Inputs

- **Project root**: `${projectPath}`
- **Runner class**: `${runnerClassName}`
- **Failing-test mvn output**: `${logPath}` (read this first to see what's failing)
- **Coverage anchor**: `${jacocoPath}` (consult to see what's already implemented for similar tests)
- **UML spec dir**: `${umlDir}` (consult `uml-communication.md` to find the pattern for the failing test; consult `uml-class-*.md` and `uml-interaction-main.md` when creating new classes/methods)

## Allowlist (per issue 141)

Modify only files under:

- `${projectPath}/src/main/java/`
- `${projectPath}/src/test/java/org/farhan/impl/`

Don't read code outside `${projectPath}`. Use logging statements to debug as needed.
