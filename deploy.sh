#!/bin/bash

# Script de despliegue para EC2
# Autor: Despliegue Automatizado
# Fecha: $(date)

set -e

echo "🚀 Iniciando despliegue de la aplicación..."

# Colores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Función para imprimir mensajes
print_status() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

print_header() {
    echo -e "${BLUE}[DEPLOY]${NC} $1"
}

print_header "Verificando dependencias..."

# Verificar si Docker está instalado
if ! command -v docker &> /dev/null; then
    print_error "Docker no está instalado. Ejecuta primero setup-ec2.sh"
    exit 1
fi

# Verificar si Docker Compose está instalado
if ! command -v docker-compose &> /dev/null; then
    print_error "Docker Compose no está instalado. Ejecuta primero setup-ec2.sh"
    exit 1
fi

# Verificar si curl está instalado (necesario para health check)
if ! command -v curl &> /dev/null; then
    print_warning "curl no está instalado. Instalando curl..."
    sudo apt-get update && sudo apt-get install -y curl
fi

# Verificar si estamos en el grupo docker
if ! groups $USER | grep -q docker; then
    print_warning "Usuario no está en el grupo docker. Ejecuta 'newgrp docker' o reinicia la sesión SSH"
fi

# Verificar conectividad con RDS
print_header "Verificando conectividad con RDS..."
if [ -f "check-rds.sh" ]; then
    chmod +x check-rds.sh
    ./check-rds.sh
else
    print_warning "Script check-rds.sh no encontrado, saltando verificación de RDS"
fi

# Cargar variables de entorno (opcional para docker-compose.yml)
if [ -f "env.prod" ]; then
    print_status "Cargando variables de entorno..."
    export $(cat env.prod | grep -v '^#' | xargs)
else
    print_warning "Archivo env.prod no encontrado, usando configuración por defecto"
fi

# Configurar Nginx
print_header "Configurando Nginx..."
if [ -f "nginx-app.conf" ]; then
    # Verificar si Nginx está instalado
    if ! command -v nginx &> /dev/null; then
        print_warning "Nginx no está instalado. Instalando Nginx..."
        sudo apt-get update
        sudo apt-get install -y nginx
    fi
    
    # Verificar si el directorio existe
    if [ ! -d "/etc/nginx/sites-available" ]; then
        print_warning "Directorio sites-available no existe. Creando estructura..."
        sudo mkdir -p /etc/nginx/sites-available
        sudo mkdir -p /etc/nginx/sites-enabled
    fi
    
    sudo cp nginx-app.conf /etc/nginx/sites-available/desarrolloweb
    sudo ln -sf /etc/nginx/sites-available/desarrolloweb /etc/nginx/sites-enabled/
    
    # Remover configuración por defecto si existe
    if [ -f "/etc/nginx/sites-enabled/default" ]; then
        sudo rm -f /etc/nginx/sites-enabled/default
    fi
    
    # Verificar configuración y recargar
    if sudo nginx -t; then
        sudo systemctl reload nginx
        print_status "Nginx configurado correctamente"
    else
        print_error "Error en la configuración de Nginx"
        exit 1
    fi
else
    print_warning "Archivo nginx-app.conf no encontrado, saltando configuración de Nginx"
fi

# Detener contenedores existentes
print_status "Deteniendo contenedores existentes..."
docker-compose down --remove-orphans || true

# Limpiar imágenes antiguas
print_status "Limpiando imágenes Docker antiguas..."
docker system prune -f

# Construir y levantar la aplicación
print_header "Construyendo y levantando la aplicación..."
docker-compose up -d --build

# Esperar a que la aplicación esté lista
print_status "Esperando a que la aplicación esté lista..."
sleep 30

# Verificar el estado de la aplicación
print_status "Verificando el estado de la aplicación..."
if curl -f http://localhost:3600/actuator/health > /dev/null 2>&1; then
    print_status "✅ Aplicación desplegada correctamente en http://localhost:3600"
    print_status "📊 Swagger UI disponible en http://localhost:3600/swagger-ui.html"
    print_status "🏥 Health check disponible en http://localhost:3600/actuator/health"
else
    print_error "❌ La aplicación no responde correctamente"
    print_status "Revisando logs..."
    docker-compose logs desarrolloweb
    exit 1
fi

# Mostrar información del sistema
print_header "Información del sistema:"
echo "  - Memoria disponible: $(free -h | grep Mem | awk '{print $7}')"
echo "  - Espacio en disco: $(df -h / | tail -1 | awk '{print $4}')"
echo "  - Contenedores activos:"
docker ps --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"

print_status "🎉 Despliegue completado exitosamente!"
print_status "🌐 Aplicación disponible en: http://localhost:3600"
print_status "📊 Swagger UI: http://localhost:3600/swagger-ui/"
print_status "🏥 Health check: http://localhost:3600/actuator/health"
print_status "🗄️  Base de datos RDS: minimarket.ckbesa4wqwo4.us-east-1.rds.amazonaws.com"

# Obtener IP pública si está disponible
PUBLIC_IP=$(curl -s ifconfig.me 2>/dev/null || echo "IP_PUBLICA")
if [ "$PUBLIC_IP" != "IP_PUBLICA" ]; then
    print_status "🌐 Aplicación disponible externamente en: http://$PUBLIC_IP:3600"
    print_status "📊 Swagger UI externo: http://$PUBLIC_IP:3600/swagger-ui/"
    print_status "🏥 Health check externo: http://$PUBLIC_IP:3600/actuator/health"
fi 