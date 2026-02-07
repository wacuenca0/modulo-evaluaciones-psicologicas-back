FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copiamos el jar ya construido desde el host al contenedor
COPY servicio-catalogos/target/servicio-catalogos-0.0.1-SNAPSHOT.jar app.jar

ENV SPRING_PROFILES_ACTIVE=prod
# Puerto por defecto del servicio-catalogos (según application.yml)
EXPOSE 8081

ENTRYPOINT ["java","-jar","app.jar"]
