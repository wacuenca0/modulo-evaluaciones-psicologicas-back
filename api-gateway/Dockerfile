# Multi-stage build para api-gateway
# Etapa de build
FROM maven:3.9.6-eclipse-temurin-21 AS build

WORKDIR /app

# Copiamos todo el backend para poder compilar el módulo con su padre
COPY . .

# Compilamos solo el módulo api-gateway (y dependencias necesarias) sin tests
RUN mvn -pl api-gateway -am -DskipTests package

# Etapa de runtime
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copiamos el jar generado del módulo api-gateway
COPY --from=build /app/api-gateway/target/api-gateway-0.0.1-SNAPSHOT.jar app.jar

ENV SPRING_PROFILES_ACTIVE=prod
EXPOSE 8080

ENTRYPOINT ["java","-jar","app.jar"]
