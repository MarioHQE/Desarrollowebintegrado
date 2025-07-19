#!/bin/bash

# Script para construir y subir imagen a Docker Hub
# Autor: Despliegue Docker Hub
# Fecha: $(date)

set -e

echo "🐳 Iniciando proceso de Docker Hub..."

# Variables
DOCKER_USERNAME="mariohqe"
IMAGE_NAME="desarrolloweb"
TAG="latest"

# Colores para output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
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

# Verificar si Docker está instalado
if ! command -v docker &> /dev/null; then
    print_error "Docker no está instalado"
    exit 1
fi

# Verificar si estás logueado en Docker Hub
if ! docker info | grep -q "Username"; then
    print_warning "No estás logueado en Docker Hub"
    print_status "Ejecuta: docker login"
    exit 1
fi

# Construir la imagen
print_status "Construyendo imagen Docker..."
docker build -t $DOCKER_USERNAME/$IMAGE_NAME:$TAG ./desarrolloweb

# Etiquetar la imagen
print_status "Etiquetando imagen..."
docker tag $DOCKER_USERNAME/$IMAGE_NAME:$TAG $DOCKER_USERNAME/$IMAGE_NAME:latest

# Subir a Docker Hub
print_status "Subiendo imagen a Docker Hub..."
docker push $DOCKER_USERNAME/$IMAGE_NAME:$TAG
docker push $DOCKER_USERNAME/$IMAGE_NAME:latest

print_status "✅ Imagen subida exitosamente a Docker Hub"
print_status "Imagen: $DOCKER_USERNAME/$IMAGE_NAME:$TAG" 