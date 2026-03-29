cd ..
call mvn clean org.farhan:sheep-dog-svc-maven-plugin:uml-to-cucumber-spring -Dtags="cucumber-gen-svc" -Dhost="dev.sheepdog.io"
cd scripts 