#!/bin/bash

# Script para actualizar la configuración de Nginx con la IP pública
echo "🌐 Actualizando configuración de Nginx con IP pública..."

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
    echo -e "${BLUE}[NGINX]${NC} $1"
}

# Obtener IP pública
print_header "Obteniendo IP pública..."
PUBLIC_IP=$(curl -s ifconfig.me 2>/dev/null)

if [ -z "$PUBLIC_IP" ]; then
    print_error "No se pudo obtener la IP pública"
    print_warning "Usando IP por defecto (cualquier IP)"
    PUBLIC_IP="_"
else
    print_status "IP pública obtenida: $PUBLIC_IP"
fi

# Crear configuración de Nginx con la IP
print_header "Creando configuración de Nginx..."
cat > nginx-app.conf << EOF
server {
    listen 80;
    server_name $PUBLIC_IP;  # IP pública del servidor
    
    # Configuración de logs
    access_log /var/log/nginx/desarrolloweb_access.log;
    error_log /var/log/nginx/desarrolloweb_error.log;
    
    # Configuración de seguridad
    add_header X-Frame-Options "SAMEORIGIN" always;
    add_header X-XSS-Protection "1; mode=block" always;
    add_header X-Content-Type-Options "nosniff" always;
    add_header Referrer-Policy "no-referrer-when-downgrade" always;
    add_header Content-Security-Policy "default-src 'self' http: https: data: blob: 'unsafe-inline'" always;
    
    # Configuración de compresión
    gzip on;
    gzip_vary on;
    gzip_min_length 1024;
    gzip_proxied expired no-cache no-store private must-revalidate auth;
    gzip_types text/plain text/css text/xml text/javascript application/x-javascript application/xml+rss application/javascript;
    
    # Configuración de timeouts
    client_max_body_size 10M;
    client_body_timeout 12;
    client_header_timeout 12;
    
    # Proxy hacia la aplicación Spring Boot
    location / {
        proxy_pass http://localhost:3600;
        proxy_http_version 1.1;
        proxy_set_header Upgrade \$http_upgrade;
        proxy_set_header Connection 'upgrade';
        proxy_set_header Host \$host;
        proxy_set_header X-Real-IP \$remote_addr;
        proxy_set_header X-Forwarded-For \$proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto \$scheme;
        proxy_cache_bypass \$http_upgrade;
        
        # Timeouts para el proxy
        proxy_connect_timeout 60s;
        proxy_send_timeout 60s;
        proxy_read_timeout 60s;
    }
    
    # Configuración específica para Swagger UI
    location /swagger-ui/ {
        proxy_pass http://localhost:3600/swagger-ui/;
        proxy_http_version 1.1;
        proxy_set_header Host \$host;
        proxy_set_header X-Real-IP \$remote_addr;
        proxy_set_header X-Forwarded-For \$proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto \$scheme;
    }
    
    # Configuración para health checks
    location /actuator/health {
        proxy_pass http://localhost:3600/actuator/health;
        proxy_http_version 1.1;
        proxy_set_header Host \$host;
        proxy_set_header X-Real-IP \$remote_addr;
        proxy_set_header X-Forwarded-For \$proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto \$scheme;
        
        # Cache para health checks
        proxy_cache_valid 200 30s;
    }
    
    # Configuración para archivos estáticos (si los tienes)
    location /static/ {
        alias /var/www/static/;
        expires 1y;
        add_header Cache-Control "public, immutable";
    }
    
    # Configuración de error pages
    error_page 404 /404.html;
    error_page 500 502 503 504 /50x.html;
    
    location = /50x.html {
        root /usr/share/nginx/html;
    }
}
EOF

print_status "Configuración de Nginx actualizada con IP: $PUBLIC_IP"

# Aplicar configuración si Nginx está instalado
if command -v nginx &> /dev/null; then
    print_header "Aplicando configuración de Nginx..."
    
    # Copiar configuración
    sudo cp nginx-app.conf /etc/nginx/sites-available/desarrolloweb
    
    # Crear enlace simbólico
    sudo ln -sf /etc/nginx/sites-available/desarrolloweb /etc/nginx/sites-enabled/
    
    # Remover configuración por defecto
    sudo rm -f /etc/nginx/sites-enabled/default
    
    # Verificar configuración
    if sudo nginx -t; then
        print_status "✅ Configuración de Nginx válida"
        
        # Recargar Nginx
        sudo systemctl reload nginx
        print_status "✅ Nginx recargado correctamente"
        
        # Verificar estado
        if systemctl is-active --quiet nginx; then
            print_status "✅ Nginx está activo"
        else
            print_error "❌ Nginx no está activo"
        fi
    else
        print_error "❌ Error en la configuración de Nginx"
        exit 1
    fi
else
    print_warning "Nginx no está instalado. La configuración se ha guardado en nginx-app.conf"
fi

print_header "URLs de acceso"
echo "  - Con Nginx: http://$PUBLIC_IP"
echo "  - Swagger UI: http://$PUBLIC_IP/swagger-ui/"
echo "  - Health check: http://$PUBLIC_IP/actuator/health"
echo "  - Directo (puerto 3600): http://$PUBLIC_IP:3600"

print_status "🎉 Configuración de Nginx completada!" 