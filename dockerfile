FROM maven:3.9.9-eclipse-temurin-21 AS build

WORKDIR /app

# Copiar el archivo pom.xml primero para aprovechar la caché de Docker
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copiar el código fuent
COPY src ./src

# Compilar la aplicación
RUN mvn clean package -DskipTests

# Segunda etapa: imagen de ejecución
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

# Exponer el puerto (Render asignará el puerto dinámicamente)
EXPOSE ${PORT:-3600}

# Configurar variables de entorno para JVM
ENV JAVA_OPTS="-Xmx512m -Xms256m"

# Usar el puerto asignado por Render
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Dserver.port=${PORT:-3600} -jar app.jar"]
