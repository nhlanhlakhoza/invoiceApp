FROM eclipse-temurin:17-jdk-alpine
VOLUME /tmp
COPY /springJwt-main/target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
EXPOSE 8081
