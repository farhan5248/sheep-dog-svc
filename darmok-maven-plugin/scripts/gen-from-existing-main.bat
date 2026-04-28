@echo off
REM All Darmok parameters are specified explicitly so `git log` on this script
REM shows exactly which parameters were used for a given run/branch. If a
REM Rebuild* branch needs different values (e.g. a different model, a tighter
REM retry budget), override here and the diff is self-documenting.
REM mvn org.farhan:darmok-maven-plugin:gen-from-existing -DmetricsDir="C:\minikube-data\darmok-metrics" -DgitBranch="main" -DmodelRefactor="opus" -DmodelGreen="opus" -DmaxClaudeSeconds="720" -DallowlistAdditionalPaths="scenarios-list-gh183.txt" -DscenariosFile="scenarios-list-gh183.txt"
cd ..
call mvn org.farhan:darmok-maven-plugin:gen-from-existing ^
    -DmetricsDir="C:\minikube-data\darmok-metrics" ^
    -DmaxRetries="3" ^
    -DmaxVerifyAttempts="3" ^
    -DretryWaitSeconds="30" ^
    -DmaxTimeoutAttempts="2" ^
    -DmaxAllowlistAttempts="2" ^
    -DbaselineVerifyEnabled="false" ^
    -DgitBranch="main" ^
    -DmodelRefactor="sonnet" ^
    -DmodelGreen="sonnet" ^
    -DmaxClaudeSeconds="720" ^
    -DallowlistAdditionalPaths="scenarios-list-gh%1%.txt,src/test/java/org/farhan/fake/,src/test/resources/captures/" ^
    -DscenariosFile="scenarios-list-gh%1%.txt"
cd scripts
