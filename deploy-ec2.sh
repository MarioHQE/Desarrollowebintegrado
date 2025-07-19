#!/bin/bash

# Script de despliegue para EC2
# Autor: Despliegue Automatizado
# Fecha: $(date)

set -e  # Salir si hay algún error

echo "🚀 Iniciando despliegue en EC2..."

# Variables de configuración
APP_NAME="desarrolloweb"
DOCKER_IMAGE_NAME="desarrolloweb-app"
CONTAINER_NAME="desarrolloweb-container"
PORT=3600

# Colores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Función para imprimir mensajes con colores
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
check_docker() {
    if ! command -v docker &> /dev/null; then
        print_error "Docker no está instalado. Instalando Docker..."
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
    docker build -t $DOCKER_IMAGE_NAME ./desarrolloweb
    print_status "Imagen construida correctamente"
}

# Ejecutar con Docker Compose
deploy_with_compose() {
    print_status "Desplegando con Docker Compose..."
    docker-compose down 2>/dev/null || true
    docker-compose up -d --build
    print_status "Aplicación desplegada con Docker Compose"
}

# Ejecutar con Docker directamente
deploy_with_docker() {
    print_status "Desplegando con Docker..."
    docker run -d \
        --name $CONTAINER_NAME \
        --restart unless-stopped \
        -p $PORT:$PORT \
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
    
    if curl -f http://localhost:$PORT/actuator/health 2>/dev/null; then
        print_status "✅ Aplicación funcionando correctamente"
    else
        print_warning "⚠️  No se pudo verificar el health check, pero la aplicación puede estar funcionando"
    fi
}

# Mostrar logs
show_logs() {
    print_status "Mostrando logs de la aplicación..."
    docker logs $CONTAINER_NAME --tail 50
}

# Función principal
main() {
    print_status "Iniciando proceso de despliegue..."
    
    # Verificar dependencias
    check_docker
    check_docker_compose
    
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
    
    # Mostrar información final
    print_status "🎉 Despliegue completado!"
    print_status "📊 Información del despliegue:"
    echo "   - Aplicación: $APP_NAME"
    echo "   - Puerto: $PORT"
    echo "   - Contenedor: $CONTAINER_NAME"
    echo "   - URL: http://$(curl -s http://169.254.169.254/latest/meta-data/public-ipv4 2>/dev/null || echo 'localhost'):$PORT"
    echo ""
    print_status "📋 Comandos útiles:"
    echo "   - Ver logs: docker logs $CONTAINER_NAME -f"
    echo "   - Detener: docker stop $CONTAINER_NAME"
    echo "   - Reiniciar: docker restart $CONTAINER_NAME"
    echo "   - Estado: docker ps"
}

# Ejecutar función principal
main "$@" 