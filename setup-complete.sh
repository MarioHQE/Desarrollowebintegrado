#!/bin/bash

# Script completo para configurar la aplicación con Nginx
echo "🚀 Configuración completa de la aplicación..."

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
    echo -e "${BLUE}[SETUP]${NC} $1"
}

# Obtener IP pública
print_header "Obteniendo IP pública..."
PUBLIC_IP=$(curl -s ifconfig.me 2>/dev/null)

if [ -z "$PUBLIC_IP" ]; then
    print_error "No se pudo obtener la IP pública"
    exit 1
else
    print_status "IP pública: $PUBLIC_IP"
fi

# Paso 1: Configurar Nginx con la IP
print_header "Paso 1: Configurando Nginx..."
chmod +x update-nginx-ip.sh
./update-nginx-ip.sh

# Paso 2: Reiniciar aplicación
print_header "Paso 2: Reiniciando aplicación..."
chmod +x restart-fixed.sh
./restart-fixed.sh

# Paso 3: Verificar Security Group
print_header "Paso 3: Verificando configuración de red..."
print_warning "IMPORTANTE: Verifica que tu Security Group de EC2 tenga estas reglas:"
echo "  - SSH (22): Tu IP"
echo "  - HTTP (80): 0.0.0.0/0"
echo "  - Custom TCP (3600): 0.0.0.0/0"

# Paso 4: Verificar conectividad
print_header "Paso 4: Verificando conectividad..."
sleep 10

print_status "Verificando aplicación local..."
if curl -f http://localhost:3600/actuator/health > /dev/null 2>&1; then
    print_status "✅ Aplicación responde localmente"
else
    print_error "❌ Aplicación no responde localmente"
fi

print_status "Verificando Nginx local..."
if curl -f http://localhost/actuator/health > /dev/null 2>&1; then
    print_status "✅ Nginx responde localmente"
else
    print_error "❌ Nginx no responde localmente"
fi

print_status "Verificando conectividad externa..."
if curl -f http://$PUBLIC_IP/actuator/health > /dev/null 2>&1; then
    print_status "✅ Aplicación accesible externamente vía Nginx"
else
    print_warning "⚠️  Aplicación no accesible externamente vía Nginx"
fi

if curl -f http://$PUBLIC_IP:3600/actuator/health > /dev/null 2>&1; then
    print_status "✅ Aplicación accesible externamente vía puerto directo"
else
    print_warning "⚠️  Aplicación no accesible externamente vía puerto directo"
fi

# Paso 5: Mostrar URLs finales
print_header "Paso 5: URLs de acceso"
echo ""
echo "🌐 URLs principales:"
echo "  - Aplicación (Nginx): http://$PUBLIC_IP"
echo "  - Swagger UI: http://$PUBLIC_IP/swagger-ui/"
echo "  - Health check: http://$PUBLIC_IP/actuator/health"
echo ""
echo "🔧 URLs directas (puerto 3600):"
echo "  - Aplicación directa: http://$PUBLIC_IP:3600"
echo "  - Swagger UI directo: http://$PUBLIC_IP:3600/swagger-ui/"
echo "  - Health check directo: http://$PUBLIC_IP:3600/actuator/health"
echo ""

# Paso 6: Información adicional
print_header "Información adicional"
echo "📊 Estado de servicios:"
systemctl is-active --quiet nginx && echo "  - Nginx: ✅ Activo" || echo "  - Nginx: ❌ Inactivo"
systemctl is-active --quiet docker && echo "  - Docker: ✅ Activo" || echo "  - Docker: ❌ Inactivo"

echo ""
echo "📋 Contenedores ejecutándose:"
docker ps --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"

echo ""
print_status "🎉 Configuración completada!"
print_warning "Si no puedes acceder externamente, verifica el Security Group de EC2" 