@echo off
REM All Darmok parameters are specified explicitly so `git log` on this script
REM shows exactly which parameters were used for a given run/branch. If a
REM Rebuild* branch needs different values (e.g. a different model, a tighter
REM retry budget), override here and the diff is self-documenting.

cd ..
call mvn org.farhan:darmok-maven-plugin:gen-from-existing ^
    -DscenariosFile="scenarios-list.txt" ^
    -Dhost="qa.sheepdog.io" ^
    -DmodelGreen="sonnet" ^
    -DmodelRefactor="sonnet" ^
    -Dstage="true" ^
    -DonlyChanges="false" ^
    -DmaxClaudeSeconds="600" ^
    -DmaxTimeoutAttempts="2" ^
    -DmetricsDir="C:\minikube-data\darmok-metrics" ^
    -DgitBranch="main"
cd scripts
