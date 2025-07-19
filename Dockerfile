# Etapa de construcción
FROM maven:3.9.9-eclipse-temurin-21 AS build

WORKDIR /app

# Copiar el archivo pom.xml primero para aprovechar la caché de Docker
COPY desarrolloweb/pom.xml .
RUN mvn dependency:go-offline -B

# Copiar el código fuente
COPY desarrolloweb/src ./src

# Compilar la aplicación
RUN mvn clean package -DskipTests

# Etapa de ejecución optimizada para Cloud Run
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copiar el JAR compilado desde la etapa anterior
COPY --from=build /app/target/desarrolloweb-0.0.1-SNAPSHOT.jar app.jar

# Crear usuario no-root para seguridad
RUN addgroup -g 1001 -S appgroup && \
    adduser -u 1001 -S appuser -G appgroup

# Cambiar propiedad del archivo JAR
RUN chown appuser:appgroup app.jar

# Cambiar al usuario no-root
USER appuser

# Exponer el puerto (Cloud Run asignará el puerto dinámicamente)
EXPOSE 8080

ENV SPRING_PROFILES_ACTIVE=prod


# Comando de inicio para Cloud Run
ENTRYPOINT [ "java", "-jar", "app.jar" ]