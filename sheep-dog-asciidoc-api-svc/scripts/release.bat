cd ..
REM -Ppublic activates the ghcr.io mirror push in the inner mvn deploy (see
REM the public profile in pom.xml). Matches the preparation_goals value passed
REM by sheep-dog-svc-release.yml for this service. See #236.
call mvn org.farhan:sheep-dog-mgmt-maven-plugin:release -DpreparationGoals="deploy,-DskipTests,-Dhelm.deploy.skip=true,-Ppublic" || exit /b 1
cd scripts
