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

# Verificar si Docker está instalado
if ! command -v docker &> /dev/null; then
    print_error "Docker no está instalado. Instalando Docker..."
    curl -fsSL https://get.docker.com -o get-docker.sh
    sh get-docker.sh
    sudo usermod -aG docker $USER
    rm get-docker.sh
    print_status "Docker instalado correctamente"
fi

# Verificar si Docker Compose está instalado
if ! command -v docker-compose &> /dev/null; then
    print_error "Docker Compose no está instalado. Instalando Docker Compose..."
    sudo curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
    sudo chmod +x /usr/local/bin/docker-compose
    print_status "Docker Compose instalado correctamente"
fi

# Verificar si curl está instalado (necesario para health check)
if ! command -v curl &> /dev/null; then
    print_warning "curl no está instalado. Instalando curl..."
    sudo apt-get update && sudo apt-get install -y curl
fi

# Cargar variables de entorno
if [ -f "env.prod" ]; then
    print_status "Cargando variables de entorno..."
    export $(cat env.prod | grep -v '^#' | xargs)
else
    print_error "Archivo env.prod no encontrado"
    exit 1
fi

# Detener contenedores existentes
print_status "Deteniendo contenedores existentes..."
docker-compose -f docker-compose.prod.yml down --remove-orphans || true

# Limpiar imágenes antiguas
print_status "Limpiando imágenes Docker antiguas..."
docker system prune -f

# Construir y levantar la aplicación
print_status "Construyendo y levantando la aplicación..."
docker-compose -f docker-compose.prod.yml up -d --build

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
    docker-compose -f docker-compose.prod.yml logs desarrolloweb
    exit 1
fi

# Mostrar información del sistema
print_status "Información del sistema:"
echo "  - Memoria disponible: $(free -h | grep Mem | awk '{print $7}')"
echo "  - Espacio en disco: $(df -h / | tail -1 | awk '{print $4}')"
echo "  - Contenedores activos: $(docker ps --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}")"

print_status "🎉 Despliegue completado exitosamente!" 