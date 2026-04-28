@echo off
REM All Darmok parameters are specified explicitly so `git log` on this script
REM shows exactly which parameters were used for a given run/branch. If a
REM Rebuild* branch needs different values (e.g. a different model, a tighter
REM retry budget), override here and the diff is self-documenting.
REM mvn org.farhan:darmok-maven-plugin:gen-from-existing -DmetricsDir="C:\minikube-data\darmok-metrics" -DgitBranch="main" -DmodelRefactor="opus" -DmodelGreen="opus" -DmaxClaudeSeconds="720" -DallowlistAdditionalPaths="scenarios-list-gh335.txt" -DscenariosFile="scenarios-list-gh335.txt"
cd ..
call mvn org.farhan:darmok-maven-plugin:gen-from-existing ^
    -DmetricsDir="C:\minikube-data\darmok-metrics" ^
    -DmaxRetries="3" ^
    -DmaxVerifyAttempts="3" ^
    -DretryWaitSeconds="30" ^
    -DmaxTimeoutAttempts="2" ^
    -DmaxAllowlistAttempts="2" ^
    -DbaselineVerifyEnabled="true" ^
    -DgitBranch="main" ^
    -DmodelRefactor="opus" ^
    -DmodelGreen="opus" ^
    -DmaxClaudeSeconds="720" ^
REM allowlistAdditionalPaths grants Darmok write access beyond the default
REM (src/main/java/, src/test/java/org/farhan/impl/) for paths that need to
REM evolve alongside the feature: the active scenarios-list, the per-command
REM fake classes (org.farhan.fake), and the captured FSM YAMLs. Remove the
REM fake/ + captures/ entries once #327 lands and they're in the default
REM allowlist.
    -DallowlistAdditionalPaths="scenarios-list-gh335.txt,src/test/java/org/farhan/fake/,src/test/resources/captures/" ^
    -DscenariosFile="scenarios-list-gh335.txt"
cd scripts
