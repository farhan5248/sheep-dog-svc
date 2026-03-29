cd ..
call mvn clean org.farhan:sheep-dog-svc-maven-plugin:uml-to-cucumber-spring -Dtags="svc-maven-plugin" -Dhost="dev.sheepdog.io"
cd scripts 