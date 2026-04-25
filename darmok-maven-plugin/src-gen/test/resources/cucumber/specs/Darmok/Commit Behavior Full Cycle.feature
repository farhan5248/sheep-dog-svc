@darmok-maven-plugin
Feature: Commit Behavior Full Cycle

  \@darmok-maven-plugin
  The full red / green / refactor cycle on a single scenario with stage false, producing three separate commits.
  Command invocations are observed via runner log entries (`Running: mvn ...`, `Executing: claude ...`, `Running: git ...`) rather than direct command assertions ? finer-grain command-level tests belong in the mocking issue.
  Preconditions: the project has a scenarios-list with one entry, the referenced asciidoc file exists without the target tag, and src/main/java does not yet contain the implementation the scenario is asking for.
  Observable outcome: the generated implementation class now exists, the scenarios-list is empty, three commits were made (one per phase), the mojo log records each phase, and the runners log records every subprocess invocation.

  Background: A pending scenario waiting to be implemented

    Given The code-prj project scenarios-list.txt file is created as follows
          """
          File: ProcessDarmok
            Scenario: User logs in successfully
              Tag: loginHappyPath
          """
      And The spec-prj project src/test/resources/asciidoc/specs/ProcessDarmok.asciidoc file is created as follows
          """
          = Test-Suite: Login
          
          == Test-Case: User logs in successfully
          
          Some description
          """
      And The code-prj project src/main/java/org/farhan/objects/LoginHappyPath.java file isn't created

  Scenario: Red fails, green and refactor succeed, one commit per phase

    The default (`stage=false`) flow gives each RGR phase its own commit so git history shows the Red ? Green ? Refactor progression explicitly.
    Someone reading the log later can walk the commits backwards to see what the failing test looked like, what code was generated to pass it, and how the refactor stabilized it.
    This Test-Case exercises the happy path end-to-end: red fails as expected, green generates the implementation, and refactor stabilizes without issues.

     When The darmok plugin gen-from-existing goal is executed and succeeds with
          | Stage | ModelGreen | ModelRefactor |
          | false | sonnet     | sonnet        |
     Then The code-prj project src/main/java/org/farhan/objects/LoginHappyPath.java file will be present
      And The code-prj project scenarios-list.txt file will be empty
      And The code-prj project darmok.mojo.log file will be as follows
          | Level | Category | Content                                                                       |
          | INFO  | mojo     | RGR Automation Plugin (gen-from-existing)                                     |
          | DEBUG | mojo     | Cleanup: Deleted 0 NUL files                                                  |
          | DEBUG | mojo     | Cleanup: Deleted target directory                                             |
          | INFO  | mojo     | Processing Scenario: ProcessDarmok/User logs in successfully [loginHappyPath] |
          | DEBUG | mojo     | Added tag                                                                     |

  @GH314
  Scenario: Refactor allowlist runs before scenarios-list mod under stage true

    \@GH314
    Pins down the issue The corollary observable lives in the runner log line order ? `Running: git status --porcelain` for the refactor phase appears before refactor's `Running: git add .` (which captures both refactor's claude output and the deferred scenarios-list delta into the same staged commit). Both logs are asserted because the mojo log proves the gate's outcome and the runner log proves the relative ordering of the gate, the scenarios-list-driven staging, and the amend.

     When The darmok plugin gen-from-existing goal is executed and succeeds with
          | Stage | ModelGreen | ModelRefactor |
          | true  | sonnet     | sonnet        |
     Then The code-prj project src/main/java/org/farhan/objects/LoginHappyPath.java file will be present
      And The code-prj project scenarios-list.txt file will be empty
      And The code-prj project darmok.mojo.log file will be as follows
          | Level | Category | Content                                      |
          | INFO  | mojo     | Green: Allowlist check running...            |
          | INFO  | mojo     | Green: Allowlist check passed, proceeding    |
          | INFO  | mojo     | Refactor: Allowlist check running...         |
          | INFO  | mojo     | Refactor: Allowlist check passed, proceeding |
          | INFO  | mojo     | Refactor: Committing                         |
      And The code-prj project darmok.runners.log file will be as follows
          | Level | Category | Content                                                                                                                                                |
          | DEBUG | runner   | Executing: claude --resume 00000000-0000-0000-0000-000000000001 --print --dangerously-skip-permissions --model sonnet /rgr-refactor forward darmok-prj |
          | DEBUG | runner   | Running: git status --porcelain                                                                                                                        |
          | DEBUG | runner   | Running: git add .                                                                                                                                     |
          | DEBUG | runner   | Running: git commit --amend --no-edit                                                                                                                  |

