cd ..
call mvn clean
call mvn org.farhan:sheep-dog-svc-maven-plugin:cucumber-to-uml -Dtags="%1"
cd scripts 