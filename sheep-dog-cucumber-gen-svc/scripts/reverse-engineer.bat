cd ..
call mvn clean
call mvn org.farhan:sheep-dog-svc-maven-plugin:cucumber-to-uml -Dtags="cucumber-gen-svc"
cd scripts 