# Multi-stage build para servicio-catalogos
# Etapa de build
FROM maven:3.9.6-eclipse-temurin-21 AS build

WORKDIR /app

# Copiamos todo el backend para poder compilar el módulo con su padre
COPY . .

# Compilamos solo el módulo servicio-catalogos (y dependencias necesarias) sin tests
RUN mvn -pl servicio-catalogos -am -DskipTests package

# Etapa de runtime
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copiamos el jar generado del módulo servicio-catalogos
COPY --from=build /app/servicio-catalogos/target/servicio-catalogos-0.0.1-SNAPSHOT.jar app.jar

ENV SPRING_PROFILES_ACTIVE=prod
# Puerto por defecto del servicio-catalogos (según application.yml)
EXPOSE 8081

ENTRYPOINT ["java","-jar","app.jar"]
