#!/bin/bash

# Script para reiniciar la aplicación con variables de entorno corregidas
echo "🔧 Reiniciando aplicación con configuración corregida..."

# Colores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

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
    echo -e "${BLUE}[RESTART]${NC} $1"
}

print_header "Deteniendo contenedores existentes..."
docker-compose down

print_header "Limpiando recursos Docker..."
docker system prune -f

print_header "Reconstruyendo y levantando aplicación..."
docker-compose up -d --build

print_header "Esperando a que la aplicación esté lista..."
sleep 45

print_header "Verificando estado de la aplicación..."
if curl -f http://localhost:3600/actuator/health > /dev/null 2>&1; then
    print_status "✅ Aplicación iniciada correctamente"
    print_status "🌐 Disponible en: http://localhost:3600"
    print_status "📊 Swagger UI: http://localhost:3600/swagger-ui/"
    print_status "🏥 Health check: http://localhost:3600/actuator/health"
else
    print_error "❌ La aplicación no responde correctamente"
    print_header "Revisando logs..."
    docker-compose logs desarrolloweb
    exit 1
fi

print_header "Mostrando logs de inicio..."
docker-compose logs --tail=20 desarrolloweb

print_status "🎉 Reinicio completado exitosamente!" 