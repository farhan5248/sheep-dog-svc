@echo off
REM All Darmok parameters are specified explicitly so `git log` on this script
REM shows exactly which parameters were used for a given run/branch. If a
REM Rebuild* branch needs different values (e.g. a different model, a tighter
REM retry budget), override here and the diff is self-documenting.

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
    -DscenariosFile="scenarios-list.txt"
cd scripts
