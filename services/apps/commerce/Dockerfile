FROM eclipse-temurin:21-jre
WORKDIR /app
COPY services/apps/commerce/build/libs/*.jar app.jar
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "/app/app.jar"]

