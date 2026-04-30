@darmok-maven-plugin
Feature: Red Phase Maven Retry

  \@darmok-maven-plugin
  The two `sheep-dog-svc-maven-plugin` goals invoked during the red phase (`asciidoctor-to-uml`, `uml-to-cucumber-guice`) talk to a remote service. When the service is unavailable, the plugin's own health-check loop times out after 300 seconds and surfaces the literal `Service did not become available within 300000 milliseconds` line in stdout. The maven retry loop wraps both goals, scans the `log.txt` written by each call for that pattern, and retries up to `maxRetries` times with `retryWaitSeconds` between attempts. Recovery on any retry returns control to the next red-phase step; exhaustion aborts the goal with `rgr-red failed`. The third red-phase maven call (`mvn test`) is excluded from the retry loop ? its non-zero exit is the red signal "tests failing, proceed to green", not an infra failure.

  Background: A failing scenario that enters the red phase

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

  @GH349
  Scenario: Asciidoctor-to-uml retries on Service-not-available then succeeds

    \@GH349
    The first red-phase maven invocation fails on attempt 1 with the retryable pattern; the loop detects the match, waits, retries, and the second invocation succeeds. The runner log records the retry detection at WARN (not ERROR, since recovery followed). Red phase proceeds to `uml-to-cucumber-guice` and the rest of the cycle as usual.

    Given The darmok plugin gen-from-existing goal mvn asciidoctor-to-uml command is executed and succeeds with
          | Pattern                                                     |
          | Service did not become available within 300000 milliseconds |
     When The darmok plugin gen-from-existing goal is executed and succeeds
     Then The code-prj project darmok.runners.log file will be as follows
          | Level | Category | Content                                                           |
          | WARN  | runner   | Retryable error detected: Service did not become available within |
          | DEBUG | runner   | Retry attempt 2 of 3...                                           |
          | DEBUG | runner   | Maven CLI completed successfully                                  |

