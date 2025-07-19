#!/bin/bash

# Script para verificar conectividad con RDS
echo "🔍 Verificando conectividad con RDS..."

# Colores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
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

# Verificar si mysql client está instalado
if ! command -v mysql &> /dev/null; then
    print_warning "MySQL client no está instalado. Instalando..."
    sudo apt-get update
    sudo apt-get install -y mysql-client
fi

# Configuración de RDS
RDS_HOST="minimarket.ckbesa4wqwo4.us-east-1.rds.amazonaws.com"
RDS_PORT="3306"
RDS_USER="adminsq"
RDS_PASSWORD="Mario14y15."
RDS_DATABASE="minimarket"

print_status "Verificando conectividad con RDS..."
print_status "Host: $RDS_HOST"
print_status "Puerto: $RDS_PORT"
print_status "Usuario: $RDS_USER"
print_status "Base de datos: $RDS_DATABASE"

# Verificar conectividad de red
print_status "Verificando conectividad de red..."
if ping -c 3 $RDS_HOST > /dev/null 2>&1; then
    print_status "✅ Conectividad de red OK"
else
    print_error "❌ No se puede hacer ping al host RDS"
    print_warning "Verifica que el Security Group de RDS permita conexiones desde tu EC2"
fi

# Verificar puerto
print_status "Verificando puerto MySQL..."
if nc -z $RDS_HOST $RDS_PORT 2>/dev/null; then
    print_status "✅ Puerto MySQL (3306) accesible"
else
    print_error "❌ Puerto MySQL (3306) no accesible"
    print_warning "Verifica que el Security Group de RDS permita conexiones en el puerto 3306"
fi

# Intentar conexión MySQL
print_status "Intentando conexión MySQL..."
if mysql -h $RDS_HOST -P $RDS_PORT -u $RDS_USER -p$RDS_PASSWORD -e "SELECT 1;" 2>/dev/null; then
    print_status "✅ Conexión MySQL exitosa"
    
    # Verificar que la base de datos existe
    if mysql -h $RDS_HOST -P $RDS_PORT -u $RDS_USER -p$RDS_PASSWORD -e "USE $RDS_DATABASE; SELECT 1;" 2>/dev/null; then
        print_status "✅ Base de datos '$RDS_DATABASE' accesible"
    else
        print_warning "⚠️  Base de datos '$RDS_DATABASE' no existe, se creará automáticamente"
    fi
else
    print_error "❌ No se puede conectar a MySQL"
    print_warning "Verifica:"
    print_warning "  - Credenciales correctas"
    print_warning "  - Security Group de RDS"
    print_warning "  - Estado de la instancia RDS"
fi

# Verificar desde Docker (opcional)
print_status "Verificando conectividad desde Docker..."
if docker run --rm mysql:8.0 mysql -h $RDS_HOST -P $RDS_PORT -u $RDS_USER -p$RDS_PASSWORD -e "SELECT 1;" 2>/dev/null; then
    print_status "✅ Conexión desde Docker exitosa"
else
    print_warning "⚠️  No se puede conectar desde Docker (esto puede ser normal si RDS requiere SSL)"
fi

echo ""
print_status "🔍 Verificación completada" 