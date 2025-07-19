#!/bin/bash

# Script para probar el despliegue en EC2

echo "🧪 Probando despliegue..."

# Obtener IP pública de EC2
EC2_IP=$(curl -s http://169.254.169.254/latest/meta-data/public-ipv4)
DOMAIN="54.210.224.54.nip.io"

echo "📍 IP pública: $EC2_IP"

# Función para probar conexión
test_connection() {
    local url=$1
    local description=$2
    
    echo "🔍 Probando $description..."
    
    if curl -s -I "$url" > /dev/null 2>&1; then
        echo "✅ $description: CONEXIÓN EXITOSA"
        return 0
    else
        echo "❌ $description: FALLA EN LA CONEXIÓN"
        return 1
    fi
}

# Función para probar SSL
test_ssl() {
    local url=$1
    local description=$2
    
    echo "🔒 Probando SSL para $description..."
    
    if curl -s -I "$url" > /dev/null 2>&1; then
        echo "✅ $description: SSL FUNCIONANDO"
        return 0
    else
        echo "❌ $description: PROBLEMA CON SSL"
        return 1
    fi
}

# Probar servicios Docker
echo "🐳 Verificando servicios Docker..."
if docker-compose -f docker-compose.prod.yml ps | grep -q "Up"; then
    echo "✅ Servicios Docker ejecutándose"
else
    echo "❌ Servicios Docker no están ejecutándose"
fi

# Probar puertos
echo "🔌 Verificando puertos..."
if netstat -tuln | grep -q ":80 "; then
    echo "✅ Puerto 80 abierto"
else
    echo "❌ Puerto 80 cerrado"
fi

if netstat -tuln | grep -q ":443 "; then
    echo "✅ Puerto 443 abierto"
else
    echo "❌ Puerto 443 cerrado"
fi

if netstat -tuln | grep -q ":8443 "; then
    echo "✅ Puerto 8443 abierto"
else
    echo "❌ Puerto 8443 cerrado"
fi

# Probar conexiones
echo "🌐 Probando conexiones..."

# Con Let's Encrypt (si hay dominio)
if [ "$DOMAIN" != "54.210.224.54.nip.io" ]; then
    test_connection "http://$DOMAIN" "HTTP con dominio"
    test_ssl "https://$DOMAIN" "HTTPS con dominio"
    test_connection "https://$DOMAIN/swagger-ui.html" "Swagger UI con dominio"
fi

# Con IP pública
test_connection "http://$EC2_IP" "HTTP con IP"
test_ssl "https://$EC2_IP:8443" "HTTPS con IP (puerto 8443)"
test_connection "https://$EC2_IP:8443/swagger-ui.html" "Swagger UI con IP"

# Probar endpoints de la API
echo "🔗 Probando endpoints de la API..."

API_BASE="https://$EC2_IP:8443"
if [ "$DOMAIN" != "54.210.224.54.nip.io" ]; then
    API_BASE="https://$DOMAIN"
fi

# Probar endpoint de salud (si existe)
if curl -s -k "$API_BASE/actuator/health" > /dev/null 2>&1; then
    echo "✅ Endpoint de salud: FUNCIONANDO"
else
    echo "⚠️  Endpoint de salud: NO DISPONIBLE"
fi

# Probar Swagger
if curl -s -k "$API_BASE/swagger-ui.html" | grep -q "Swagger"; then
    echo "✅ Swagger UI: FUNCIONANDO"
else
    echo "❌ Swagger UI: NO DISPONIBLE"
fi

# Probar API docs
if curl -s -k "$API_BASE/v3/api-docs" | grep -q "openapi"; then
    echo "✅ API Docs: FUNCIONANDO"
else
    echo "❌ API Docs: NO DISPONIBLE"
fi

echo ""
echo "📊 Resumen de URLs:"
echo "🌐 Aplicación: $API_BASE"
echo "📚 Swagger: $API_BASE/swagger-ui.html"
echo "📖 API Docs: $API_BASE/v3/api-docs"

echo ""
echo "✅ Pruebas completadas!" 