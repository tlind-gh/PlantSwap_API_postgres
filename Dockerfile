FROM maven:3.8.4-openjdk-17-slim AS build
WORKDIR /app
COPY pom.xml .
# Ladda ner alla dependencies i ett separat lager
RUN mvn dependency:go-offline

# Kopiera källkoden och bygg
COPY src ./src
RUN mvn package -DskipTests

# Runtime stage
FROM openjdk:17-jdk-slim
WORKDIR /app
# Kopiera bara JAR-filen från build stage
COPY --from=build /app/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]