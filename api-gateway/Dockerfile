FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copiamos el jar ya construido desde el host al contenedor
COPY api-gateway/target/api-gateway-0.0.1-SNAPSHOT.jar app.jar

ENV SPRING_PROFILES_ACTIVE=prod
EXPOSE 8080

ENTRYPOINT ["java","-jar","app.jar"]
