FROM maven:3.9.6-eclipse-temurin-21 AS build

WORKDIR /app
COPY . .

# This builds your Spring Boot JAR
RUN mvn clean package -DskipTests

# ---- Step 2: Run the app ----
FROM eclipse-temurin:21-jdk

WORKDIR /app

# Copy only the built JAR from the previous stage
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]
