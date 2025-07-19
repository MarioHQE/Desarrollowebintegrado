#!/bin/bash

# Script de despliegue para EC2 con SSL autofirmado
# Ejecutar en la instancia EC2

set -e

echo "🚀 Iniciando despliegue en EC2 con SSL autofirmado..."

# Obtener IP pública
EC2_IP=$(curl -s http://169.254.169.254/latest/meta-data/public-ipv4)
echo "📍 IP pública: $EC2_IP"

# Actualizar sistema
echo "📦 Actualizando sistema..."
sudo yum update -y

# Instalar Docker
echo "🐳 Instalando Docker..."
if ! command -v docker &> /dev/null; then
    sudo yum install -y docker
    sudo systemctl start docker
    sudo systemctl enable docker
    sudo usermod -a -G docker ec2-user
    echo "✅ Docker instalado"
else
    echo "✅ Docker ya está instalado"
fi

# Instalar Docker Compose
echo "📋 Instalando Docker Compose..."
if ! command -v docker-compose &> /dev/null; then
    sudo curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
    sudo chmod +x /usr/local/bin/docker-compose
    echo "✅ Docker Compose instalado"
else
    echo "✅ Docker Compose ya está instalado"
fi

# Configurar firewall
echo "🔥 Configurando firewall..."
if command -v firewall-cmd &> /dev/null; then
    sudo systemctl start firewalld
    sudo systemctl enable firewalld
    sudo firewall-cmd --permanent --add-port=8443/tcp
    sudo firewall-cmd --reload
    echo "✅ Firewall configurado"
else
    echo "⚠️  Firewalld no disponible, configurando con iptables..."
    sudo yum install -y iptables-services
    sudo systemctl start iptables
    sudo systemctl enable iptables
    sudo iptables -A INPUT -p tcp --dport 8443 -j ACCEPT
    sudo service iptables save
fi

# Detener servicios existentes
echo "🛑 Deteniendo servicios existentes..."
docker-compose -f docker-compose.ssl-selfsigned.yml down 2>/dev/null || true

# Iniciar aplicación con SSL autofirmado
echo "🔒 Iniciando aplicación con SSL autofirmado..."
docker-compose -f docker-compose.ssl-selfsigned.yml up -d

# Esperar a que la aplicación esté lista
echo "⏳ Esperando a que la aplicación esté lista..."
sleep 30

# Verificar servicios
echo "🔍 Verificando servicios..."
docker-compose -f docker-compose.ssl-selfsigned.yml ps

# Probar conexión
echo "🧪 Probando conexión..."
if curl -s -k -I "https://$EC2_IP:8443" > /dev/null 2>&1; then
    echo "✅ Conexión HTTPS exitosa"
else
    echo "❌ Error en conexión HTTPS"
fi

echo ""
echo "🎉 Despliegue completado exitosamente!"
echo "🌐 URL: https://$EC2_IP:8443"
echo "📊 Swagger: https://$EC2_IP:8443/swagger-ui.html"
echo "📖 API Docs: https://$EC2_IP:8443/v3/api-docs/"
echo ""
echo "⚠️  Nota: El navegador mostrará una advertencia de seguridad por el certificado autofirmado"
echo "💡 Para aceptar el certificado, haz clic en 'Avanzado' y luego 'Continuar'" 