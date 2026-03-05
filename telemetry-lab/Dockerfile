FROM gradle:8.5-jdk21 AS build

WORKDIR /app

# Copiar archivos de configuración de Gradle
COPY ./build.gradle ./build.gradle
COPY ./settings.gradle ./settings.gradle

# Copiar código fuente en la estructura correcta
COPY ./src/main ./src/main

# Construir la aplicación
RUN gradle build --no-daemon -x test

FROM eclipse-temurin:21-jre-alpine AS prod

WORKDIR /app

COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
