# > Builder builds the application from source code
#   Setting up cache with buildkit would be useful, however it's omitted for simplicity
FROM maven:3.9-eclipse-temurin-23-alpine as builder
ARG MODULE
WORKDIR /application

COPY ./pom.xml ./
COPY ./sb-access-monitor ./sb-access-monitor
COPY ./sb-access-service ./sb-access-service
RUN mvn clean package -Dmaven.test.skip=true -pl ${MODULE} -am

# > Optimizer builds the optimized image by utilizing layered jar
FROM eclipse-temurin:23-jdk-alpine as optimizer
ARG MODULE
WORKDIR /application
COPY --from=builder /application/${MODULE}/target/*.jar application.jar
RUN java -Djarmode=tools -jar application.jar extract --layers --destination extracted

# > Final image is optimized for runtime
FROM eclipse-temurin:23-jre-alpine
WORKDIR /application
COPY --from=optimizer /application/extracted/dependencies/ ./
COPY --from=optimizer /application/extracted/spring-boot-loader/ ./
COPY --from=optimizer /application/extracted/snapshot-dependencies/ ./
COPY --from=optimizer /application/extracted/application/ ./
ENTRYPOINT ["java", "-jar", "application.jar"]

