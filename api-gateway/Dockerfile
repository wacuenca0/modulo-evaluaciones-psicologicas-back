FROM eclipse-temurin:21-jdk-alpine AS build

RUN apk add --no-cache curl

WORKDIR /workspace

COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
COPY api-gateway/ api-gateway/
COPY servicio-catalogos/ servicio-catalogos/
COPY servicio-gestion/ servicio-gestion/

RUN chmod +x mvnw \
	&& ./mvnw -DskipTests -pl api-gateway -am package

FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

RUN apk add --no-cache curl

COPY --from=build /workspace/api-gateway/target/api-gateway-0.0.1-SNAPSHOT.jar app.jar

ENV SPRING_PROFILES_ACTIVE=prod
EXPOSE 8080

ENTRYPOINT ["java","-jar","app.jar"]
