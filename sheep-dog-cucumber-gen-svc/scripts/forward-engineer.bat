cd ..
call mvn clean org.farhan:sheep-dog-dev-svc-maven-plugin:uml-to-cucumber-spring -Dtags="round-trip" -Dhost="dev.sheepdogdev.io"
cd scripts 