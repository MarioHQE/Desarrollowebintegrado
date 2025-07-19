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
EXPOSE ${PORT:-8080}

# Configurar variables de entorno para JVM optimizadas para Cloud Run
ENV JAVA_OPTS="-Xmx512m -Xms256m -XX:+UseG1GC -XX:MaxGCPauseMillis=200"

# Comando de inicio para Cloud Run
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Dserver.port=${PORT:-8080} -jar app.jar"] 