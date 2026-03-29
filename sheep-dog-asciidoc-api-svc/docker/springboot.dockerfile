FROM ${docker.registry.github}/farhan5248/sheep-dog-dev-svc-dependencies:latest
COPY maven/target/dependency/META-INF /app/META-INF
COPY maven/target/dependency/BOOT-INF/classes /app/classes
COPY maven/target/dependency-org-farhan/BOOT-INF/lib /app/lib
ENV JAVA_OPTS="-Xmx512m -Xms256m"
ENTRYPOINT java $JAVA_OPTS -cp "app/classes:app/lib/*" org.farhan.mbt.RestServiceApplication
