#!/bin/bash

echo "🔍 Obteniendo IP pública del servidor..."

# Intentar diferentes métodos para obtener la IP
echo "Método 1: ifconfig.me"
IP1=$(curl -s ifconfig.me 2>/dev/null)
echo "IP obtenida: $IP1"

echo ""
echo "Método 2: ipinfo.io"
IP2=$(curl -s ipinfo.io/ip 2>/dev/null)
echo "IP obtenida: $IP2"

echo ""
echo "Método 3: checkip.amazonaws.com"
IP3=$(curl -s checkip.amazonaws.com 2>/dev/null)
echo "IP obtenida: $IP3"

echo ""
echo "Método 4: icanhazip.com"
IP4=$(curl -s icanhazip.com 2>/dev/null)
echo "IP obtenida: $IP4"

echo ""
echo "📋 Resumen de IPs obtenidas:"
echo "  - ifconfig.me: $IP1"
echo "  - ipinfo.io: $IP2"
echo "  - checkip.amazonaws.com: $IP3"
echo "  - icanhazip.com: $IP4"

# Usar la primera IP válida
if [ ! -z "$IP1" ]; then
    PUBLIC_IP=$IP1
elif [ ! -z "$IP2" ]; then
    PUBLIC_IP=$IP2
elif [ ! -z "$IP3" ]; then
    PUBLIC_IP=$IP3
elif [ ! -z "$IP4" ]; then
    PUBLIC_IP=$IP4
else
    echo "❌ No se pudo obtener la IP pública"
    exit 1
fi

echo ""
echo "✅ IP pública seleccionada: $PUBLIC_IP"
echo ""
echo "🌐 URLs de acceso:"
echo "  - Con Nginx: http://$PUBLIC_IP"
echo "  - Swagger UI: http://$PUBLIC_IP/swagger-ui/"
echo "  - Directo: http://$PUBLIC_IP:3600" 