FROM eclipse-temurin:21-jdk-alpine
LABEL "maintainer"=farhan.sheikh.5248@gmail.com
RUN apk --no-cache add curl
# TODO add the security context in k8s deployment to allow the container to write to the pv
# RUN addgroup -S spring && adduser -S spring -G spring
# USER spring:spring
COPY maven/target/dependency/BOOT-INF/lib /app/lib
