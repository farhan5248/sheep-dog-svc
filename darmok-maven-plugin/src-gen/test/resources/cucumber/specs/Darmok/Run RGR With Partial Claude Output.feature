@darmok-maven-plugin
Feature: Run RGR With Partial Claude Output

  \@darmok-maven-plugin
  ClaudeRunner mirrors every line of subprocess stdout to the runner log even when the subprocess then exits non-zero. Streamed reading doesn't drop data just because the exit was a failure ? the partial output that preceded the failure is preserved, followed by the failure marker. This preserves diagnostic context when investigating claude failures after the fact.

  Background: A failing scenario that enters the green phase

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

  Scenario: Partial claude stdout is mirrored before the failure marker

    Claude writes three lines of stdout and then the process exits 137 (e.g. killed by the OS). All three lines must appear in the runner log in the order they were produced, followed by the failure marker. If the streamed reader were to drop data on failure, downstream diagnostics would be blind to what claude was doing just before the kill.

    Given The darmok plugin gen-from-existing goal claude /rgr-green command is executed but failed non-retryably
          | Exit | Output                       |
          | 137  | line one of partial output   |
          | 137  | line two of partial output   |
          | 137  | line three of partial output |
     When The darmok plugin gen-from-existing goal is executed
     Then The code-prj project target/darmok/darmok.runners.log file will be as follows with this failure
          | Level | Category | Content                         |
          | DEBUG | runner   | line one of partial output      |
          | DEBUG | runner   | line two of partial output      |
          | DEBUG | runner   | line three of partial output    |
          | DEBUG | runner   | Claude CLI exited with code 137 |

