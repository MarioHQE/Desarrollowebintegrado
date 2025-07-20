# Etapa de construcción
FROM maven:3.9.6-eclipse-temurin-21 AS build

WORKDIR /app

# Copiar archivos de configuración de Maven
COPY desarrolloweb/pom.xml .
COPY desarrolloweb/src ./src

# Construir la aplicación
RUN mvn clean package -DskipTests

# Etapa de ejecución
FROM eclipse-temurin:21-jre

WORKDIR /app

# Copiar el JAR construido
COPY --from=build /app/target/*.jar app.jar

# Exponer el puerto
EXPOSE 8080

# Variables de entorno
ENV SPRING_PROFILES_ACTIVE=prod

# Comando de ejecución
ENTRYPOINT ["java", "-jar", "app.jar"]