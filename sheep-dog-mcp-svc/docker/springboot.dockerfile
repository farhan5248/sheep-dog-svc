FROM ${docker.registry}/sheep-dog-mcp-svc-dependencies:latest
COPY maven/target/dependency/META-INF /app/META-INF
COPY maven/target/dependency/BOOT-INF/classes /app/classes
# TODO make these two variables configurable
ENTRYPOINT ["java","-Xmx512m","-Xms256m","-cp","app/classes:app/lib/*","org.farhan.mbt.McpServerApplication"]