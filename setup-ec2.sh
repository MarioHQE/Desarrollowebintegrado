#!/bin/bash

# Script de configuración inicial para EC2
# Este script debe ejecutarse una sola vez al crear la instancia

set -e

echo "🔧 Configurando instancia EC2 para el despliegue..."

# Colores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Función para imprimir mensajes
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

# Actualizar el sistema
print_header "Actualizando el sistema..."
sudo apt-get update && sudo apt-get upgrade -y

# Instalar dependencias básicas
print_header "Instalando dependencias básicas..."
sudo apt-get install -y \
    curl \
    wget \
    git \
    unzip \
    software-properties-common \
    apt-transport-https \
    ca-certificates \
    gnupg \
    lsb-release

# Instalar Docker
print_header "Instalando Docker..."
if ! command -v docker &> /dev/null; then
    # Agregar repositorio oficial de Docker
    curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo gpg --dearmor -o /usr/share/keyrings/docker-archive-keyring.gpg
    
    echo "deb [arch=$(dpkg --print-architecture) signed-by=/usr/share/keyrings/docker-archive-keyring.gpg] https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable" | sudo tee /etc/apt/sources.list.d/docker.list > /dev/null
    
    sudo apt-get update
    sudo apt-get install -y docker-ce docker-ce-cli containerd.io
    
    # Agregar usuario actual al grupo docker
    sudo usermod -aG docker $USER
    
    print_status "Docker instalado correctamente"
else
    print_status "Docker ya está instalado"
fi

# Instalar Docker Compose
print_header "Instalando Docker Compose..."
if ! command -v docker-compose &> /dev/null; then
    sudo curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
    sudo chmod +x /usr/local/bin/docker-compose
    print_status "Docker Compose instalado correctamente"
else
    print_status "Docker Compose ya está instalado"
fi

# Instalar Nginx (opcional, para proxy reverso)
print_header "Instalando Nginx..."
sudo apt-get install -y nginx

# Configurar firewall
print_header "Configurando firewall..."
sudo ufw allow 22/tcp    # SSH
sudo ufw allow 80/tcp    # HTTP
sudo ufw allow 443/tcp   # HTTPS
sudo ufw allow 3600/tcp  # Puerto de la aplicación
sudo ufw --force enable

# Crear directorio para la aplicación
print_header "Creando estructura de directorios..."
sudo mkdir -p /opt/desarrolloweb
sudo chown $USER:$USER /opt/desarrolloweb

# Configurar variables de entorno del sistema
print_header "Configurando variables de entorno..."
echo 'export JAVA_OPTS="-Xmx1g -Xms512m"' >> ~/.bashrc
echo 'export SPRING_PROFILES_ACTIVE=docker' >> ~/.bashrc

# Instalar herramientas de monitoreo
print_header "Instalando herramientas de monitoreo..."
sudo apt-get install -y htop tree

# Configurar logrotate para Docker
print_header "Configurando rotación de logs..."
sudo tee /etc/logrotate.d/docker-container > /dev/null <<EOF
/var/lib/docker/containers/*/*.log {
    rotate 7
    daily
    compress
    size=1M
    missingok
    delaycompress
    copytruncate
}
EOF

print_status "✅ Configuración inicial completada!"
print_status "🔄 Reinicia la sesión SSH para que los cambios de grupo surtan efecto"
print_status "📁 El directorio de la aplicación será: /opt/desarrolloweb"
print_status "🌐 Nginx está configurado en el puerto 80"
print_status "🔒 Firewall configurado para puertos: 22, 80, 443, 3600"

echo ""
print_warning "IMPORTANTE: Ejecuta 'newgrp docker' o reinicia la sesión SSH antes de continuar" 