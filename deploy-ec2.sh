#!/bin/bash

# Script de despliegue para EC2 con SSL
# Ejecutar en la instancia EC2

set -e

echo "🚀 Iniciando despliegue en EC2..."

# Variables de configuración
EC2_IP="54.210.224.54"
DOMAIN="${EC2_IP}.nip.io"
EMAIL="marioelpro08@gmail.com"

echo "📍 IP: $EC2_IP"
echo "🌐 Dominio: $DOMAIN"

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

# Crear directorios necesarios
echo "📁 Creando directorios..."
mkdir -p nginx/conf.d
mkdir -p certbot/conf
mkdir -p certbot/www

# Configurar firewall
echo "🔥 Configurando firewall..."
if command -v firewall-cmd &> /dev/null; then
    sudo systemctl start firewalld
    sudo systemctl enable firewalld
    sudo firewall-cmd --permanent --add-service=http
    sudo firewall-cmd --permanent --add-service=https
    sudo firewall-cmd --reload
    echo "✅ Firewall configurado"
else
    echo "⚠️  Firewalld no disponible, configurando con iptables..."
    sudo yum install -y iptables-services
    sudo systemctl start iptables
    sudo systemctl enable iptables
    sudo iptables -A INPUT -p tcp --dport 80 -j ACCEPT
    sudo iptables -A INPUT -p tcp --dport 443 -j ACCEPT
    sudo service iptables save
fi

# Detener servicios existentes
echo "🛑 Deteniendo servicios existentes..."
docker-compose -f docker-compose.prod.yml down 2>/dev/null || true

# Iniciar Nginx primero
echo "🌐 Iniciando Nginx..."
docker-compose -f docker-compose.prod.yml up -d nginx

# Esperar a que Nginx esté listo
echo "⏳ Esperando a que Nginx esté listo..."
sleep 10

# Obtener certificado SSL
echo "📜 Obteniendo certificado SSL para $DOMAIN..."
docker-compose -f docker-compose.prod.yml run --rm certbot certonly \
    --webroot \
    --webroot-path=/var/www/certbot \
    --email $EMAIL \
    --agree-tos \
    --no-eff-email \
    -d $DOMAIN \
    --force-renewal

# Verificar certificado
if [ -d "certbot/conf/live/$DOMAIN" ]; then
    echo "✅ Certificado SSL obtenido exitosamente"
else
    echo "❌ Error al obtener certificado SSL"
    echo "🔍 Verificando logs..."
    docker-compose -f docker-compose.prod.yml logs certbot
    exit 1
fi

# Reiniciar Nginx con SSL
echo "🔄 Reiniciando Nginx con SSL..."
docker-compose -f docker-compose.prod.yml restart nginx

# Iniciar aplicación Spring Boot
echo "☕ Iniciando aplicación Spring Boot..."
docker-compose -f docker-compose.prod.yml up -d desarrolloweb

# Esperar a que la aplicación esté lista
echo "⏳ Esperando a que la aplicación esté lista..."
sleep 30

# Verificar servicios
echo "🔍 Verificando servicios..."
docker-compose -f docker-compose.prod.yml ps

# Probar conexión
echo "🧪 Probando conexión..."
if curl -s -I "https://$DOMAIN" > /dev/null 2>&1; then
    echo "✅ Conexión HTTPS exitosa"
else
    echo "❌ Error en conexión HTTPS"
fi

# Script para renovar certificados
echo "🔄 Configurando renovación automática de SSL..."
cat > renew-ssl.sh << 'EOF'
#!/bin/bash
echo "🔄 Renovando certificado SSL..."
docker-compose -f docker-compose.prod.yml run --rm certbot renew
docker-compose -f docker-compose.prod.yml restart nginx
echo "✅ Renovación completada"
EOF

chmod +x renew-ssl.sh

# Agregar renovación automática al crontab
(crontab -l 2>/dev/null; echo "0 12 * * * /home/ec2-user/renew-ssl.sh >> /home/ec2-user/ssl-renewal.log 2>&1") | crontab -

echo ""
echo "🎉 Despliegue completado exitosamente!"
echo "🌐 URL: https://$DOMAIN"
echo "📊 Swagger: https://$DOMAIN/swagger-ui.html"
echo "📖 API Docs: https://$DOMAIN/v3/api-docs/"
echo ""
echo "🔄 Renovación automática de SSL configurada (diaria a las 12:00)"
echo "📝 Logs de renovación: /home/ec2-user/ssl-renewal.log" 