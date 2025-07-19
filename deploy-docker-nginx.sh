#!/bin/bash

# Script de despliegue con Docker y Nginx en EC2
# Para proyectos Spring Boot existentes

set -e

echo "🚀 Desplegando aplicación Spring Boot con Docker y Nginx..."

# Variables de configuración
APP_NAME="desarrolloweb"
DOCKER_IMAGE_NAME="desarrolloweb-app"
CONTAINER_NAME="desarrolloweb-container"
APP_PORT=3600
NGINX_PORT=80
DOMAIN="tu-dominio.com"  # Cambiar por tu dominio real

# Colores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

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
    echo -e "${BLUE}=== $1 ===${NC}"
}

# Verificar si Docker está instalado
check_docker() {
    if ! command -v docker &> /dev/null; then
        print_error "Docker no está instalado. Instalando..."
        install_docker
    else
        print_status "Docker ya está instalado"
    fi
}

# Instalar Docker
install_docker() {
    print_status "Instalando Docker..."
    sudo yum update -y
    sudo yum install -y docker
    sudo service docker start
    sudo systemctl enable docker
    sudo usermod -a -G docker ec2-user
    print_status "Docker instalado correctamente"
}

# Verificar si Docker Compose está instalado
check_docker_compose() {
    if ! command -v docker-compose &> /dev/null; then
        print_error "Docker Compose no está instalado. Instalando..."
        install_docker_compose
    else
        print_status "Docker Compose ya está instalado"
    fi
}

# Instalar Docker Compose
install_docker_compose() {
    print_status "Instalando Docker Compose..."
    sudo curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
    sudo chmod +x /usr/local/bin/docker-compose
    print_status "Docker Compose instalado correctamente"
}

# Instalar y configurar Nginx
setup_nginx() {
    print_header "Configurando Nginx"
    
    if ! command -v nginx &> /dev/null; then
        print_status "Instalando Nginx..."
        sudo yum install -y nginx
        sudo systemctl enable nginx
        sudo systemctl start nginx
    else
        print_status "Nginx ya está instalado"
    fi
    
    # Crear configuración de Nginx
    print_status "Creando configuración de Nginx..."
    sudo tee /etc/nginx/conf.d/desarrolloweb.conf << 'EOF'
upstream spring_app {
    server localhost:3600;
    keepalive 32;
}

server {
    listen 80;
    server_name _;
    
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
    gzip_types text/plain text/css text/xml text/javascript application/x-javascript application/xml+rss application/javascript application/json;
    
    # Configuración de timeouts
    client_max_body_size 10M;
    client_body_timeout 12;
    client_header_timeout 12;
    
    # Proxy a la aplicación Spring Boot
    location / {
        proxy_pass http://spring_app;
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection 'upgrade';
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        proxy_cache_bypass $http_upgrade;
        proxy_read_timeout 86400;
        proxy_connect_timeout 60;
        proxy_send_timeout 60;
    }
    
    # Configuración para archivos estáticos (si los hay)
    location /static/ {
        alias /var/www/static/;
        expires 1y;
        add_header Cache-Control "public, immutable";
    }
    
    # Health check endpoint
    location /health {
        proxy_pass http://spring_app/actuator/health;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
    
    # Swagger UI (si está habilitado)
    location /swagger-ui/ {
        proxy_pass http://spring_app/swagger-ui/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
EOF

    # Verificar configuración de Nginx
    print_status "Verificando configuración de Nginx..."
    sudo nginx -t
    
    # Reiniciar Nginx
    print_status "Reiniciando Nginx..."
    sudo systemctl restart nginx
    
    # Configurar firewall para Nginx
    print_status "Configurando firewall..."
    sudo firewall-cmd --permanent --add-service=http
    sudo firewall-cmd --permanent --add-service=https
    sudo firewall-cmd --reload
}

# Detener y eliminar contenedores existentes
cleanup_containers() {
    print_status "Limpiando contenedores existentes..."
    docker stop $CONTAINER_NAME 2>/dev/null || true
    docker rm $CONTAINER_NAME 2>/dev/null || true
    docker system prune -f
}

# Construir la imagen Docker
build_image() {
    print_status "Construyendo imagen Docker..."
    cd desarrolloweb
    docker build -t $DOCKER_IMAGE_NAME .
    cd ..
    print_status "Imagen construida correctamente"
}

# Desplegar con Docker Compose
deploy_with_compose() {
    print_status "Desplegando con Docker Compose..."
    docker-compose down 2>/dev/null || true
    docker-compose up -d --build
    print_status "Aplicación desplegada con Docker Compose"
}

# Desplegar con Docker directamente
deploy_with_docker() {
    print_status "Desplegando con Docker..."
    docker run -d \
        --name $CONTAINER_NAME \
        --restart unless-stopped \
        -p $APP_PORT:$APP_PORT \
        -e DATABASE_URL=jdbc:mysql://minimarket.ckbesa4wqwo4.us-east-1.rds.amazonaws.com:3306/minimarket?createDatabaseIfNotExist=true \
        -e DATABASE_USERNAME=adminsq \
        -e DATABASE_PASSWORD=Mario14y15. \
        -e SPRING_PROFILES_ACTIVE=docker \
        -e STRIPE_SECRET_KEY=sk_test_51RRpwwRS5ApzbFdePxJPtFPIU5SoWXi6a9r3BfW92TNd6cPSTMf3n3BfnazI9t0KBir2VbNPYLifXVjSVUmPrlPX001PqO7jS7 \
        -e STRIPE_WEBHOOK_SECRET=whsec_9850a314cfa962c23f3616368e571af42d3d78f675cac0825c67d2d5bdffbee5 \
        -e AWS_ACCESS_KEY=AKIAWYOPDF3QT7NDAJUC \
        -e AWS_SECRET_KEY=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 \
        -e MAIL_USERNAME=marioelpro08@gmail.com \
        -e MAIL_PASSWORD=bmch\ rbeg\ tfmx\ ondv \
        $DOCKER_IMAGE_NAME
    print_status "Aplicación desplegada con Docker"
}

# Verificar el estado de la aplicación
check_health() {
    print_status "Verificando estado de la aplicación..."
    sleep 30  # Esperar a que la aplicación se inicie
    
    # Verificar aplicación directamente
    if curl -f http://localhost:$APP_PORT/actuator/health 2>/dev/null; then
        print_status "✅ Aplicación Spring Boot funcionando correctamente"
    else
        print_warning "⚠️  No se pudo verificar la aplicación directamente"
    fi
    
    # Verificar a través de Nginx
    if curl -f http://localhost/health 2>/dev/null; then
        print_status "✅ Nginx proxy funcionando correctamente"
    else
        print_warning "⚠️  No se pudo verificar Nginx proxy"
    fi
}

# Configurar SSL con Let's Encrypt (opcional)
setup_ssl() {
    read -p "¿Deseas configurar SSL con Let's Encrypt? (y/n): " -n 1 -r
    echo
    if [[ $REPLY =~ ^[Yy]$ ]]; then
        print_header "Configurando SSL con Let's Encrypt"
        
        # Instalar Certbot
        print_status "Instalando Certbot..."
        sudo yum install -y certbot python3-certbot-nginx
        
        # Obtener certificado SSL
        print_status "Obteniendo certificado SSL..."
        sudo certbot --nginx -d $DOMAIN --non-interactive --agree-tos --email tu-email@example.com
        
        # Configurar renovación automática
        print_status "Configurando renovación automática..."
        (crontab -l 2>/dev/null; echo "0 12 * * * /usr/bin/certbot renew --quiet") | crontab -
        
        print_status "SSL configurado correctamente"
    fi
}

# Mostrar información final
show_info() {
    print_header "Información del Despliegue"
    print_status "🎉 Despliegue completado!"
    echo ""
    print_status "📊 Información del despliegue:"
    echo "   - Aplicación: $APP_NAME"
    echo "   - Puerto de la aplicación: $APP_PORT"
    echo "   - Puerto de Nginx: $NGINX_PORT"
    echo "   - Contenedor: $CONTAINER_NAME"
    echo "   - IP pública: $(curl -s http://169.254.169.254/latest/meta-data/public-ipv4 2>/dev/null || echo 'localhost')"
    echo ""
    print_status "🌐 URLs de acceso:"
    echo "   - Aplicación directa: http://$(curl -s http://169.254.169.254/latest/meta-data/public-ipv4 2>/dev/null || echo 'localhost'):$APP_PORT"
    echo "   - A través de Nginx: http://$(curl -s http://169.254.169.254/latest/meta-data/public-ipv4 2>/dev/null || echo 'localhost')"
    echo "   - Health check: http://$(curl -s http://169.254.169.254/latest/meta-data/public-ipv4 2>/dev/null || echo 'localhost')/health"
    echo "   - Swagger UI: http://$(curl -s http://169.254.169.254/latest/meta-data/public-ipv4 2>/dev/null || echo 'localhost')/swagger-ui/"
    echo ""
    print_status "📋 Comandos útiles:"
    echo "   - Ver logs de la app: docker logs $CONTAINER_NAME -f"
    echo "   - Ver logs de Nginx: sudo tail -f /var/log/nginx/desarrolloweb_access.log"
    echo "   - Reiniciar app: docker restart $CONTAINER_NAME"
    echo "   - Reiniciar Nginx: sudo systemctl restart nginx"
    echo "   - Ver estado: docker ps && sudo systemctl status nginx"
    echo ""
    print_status "🔧 Archivos de configuración:"
    echo "   - Nginx config: /etc/nginx/conf.d/desarrolloweb.conf"
    echo "   - Nginx logs: /var/log/nginx/desarrolloweb_*.log"
    echo "   - Docker logs: docker logs $CONTAINER_NAME"
}

# Función principal
main() {
    print_status "Iniciando proceso de despliegue con Docker y Nginx..."
    
    # Verificar dependencias
    check_docker
    check_docker_compose
    
    # Configurar Nginx
    setup_nginx
    
    # Limpiar contenedores existentes
    cleanup_containers
    
    # Construir imagen
    build_image
    
    # Desplegar (elegir método)
    if [ -f "docker-compose.yml" ]; then
        deploy_with_compose
    else
        deploy_with_docker
    fi
    
    # Verificar estado
    check_health
    
    # Configurar SSL (opcional)
    setup_ssl
    
    # Mostrar información final
    show_info
}

# Ejecutar función principal
main "$@" 