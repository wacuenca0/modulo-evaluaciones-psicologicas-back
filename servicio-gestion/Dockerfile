FROM eclipse-temurin:21-jdk-alpine AS build

RUN apk add --no-cache curl

WORKDIR /workspace

# Compilamos dentro de la imagen para evitar JARs desactualizados
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
COPY api-gateway/ api-gateway/
COPY servicio-catalogos/ servicio-catalogos/
COPY servicio-gestion/ servicio-gestion/

RUN chmod +x mvnw \
	&& ./mvnw -DskipTests -pl servicio-gestion -am package

FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

RUN apk add --no-cache curl

COPY --from=build /workspace/servicio-gestion/target/servicio-gestion-0.0.1-SNAPSHOT.jar app.jar

ENV SPRING_PROFILES_ACTIVE=prod
# Puerto por defecto del servicio-gestion (según application.yml)
EXPOSE 8082

ENTRYPOINT ["java","-jar","app.jar"]
