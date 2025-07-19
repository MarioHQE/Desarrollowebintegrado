#!/bin/bash

# Script de monitoreo para la aplicación con RDS
# Uso: ./monitor-rds.sh [status|logs|restart|cleanup]

set -e

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
    echo -e "${BLUE}[MONITOR]${NC} $1"
}

# Función para mostrar el estado
show_status() {
    print_header "Estado del sistema"
    
    echo ""
    print_status "📊 Información del sistema:"
    echo "  - CPU: $(top -bn1 | grep "Cpu(s)" | awk '{print $2}' | cut -d'%' -f1)%"
    echo "  - Memoria: $(free -h | grep Mem | awk '{print $3"/"$2 " (" $3/$2*100.0 "%")}')"
    echo "  - Disco: $(df -h / | tail -1 | awk '{print $3"/"$2 " (" $5 ")"}')"
    echo "  - Uptime: $(uptime -p)"
    
    echo ""
    print_status "🐳 Estado de Docker:"
    if systemctl is-active --quiet docker; then
        echo "  - Docker: ✅ Activo"
    else
        echo "  - Docker: ❌ Inactivo"
    fi
    
    echo ""
    print_status "📦 Contenedores:"
    if docker ps --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}" | grep -q .; then
        docker ps --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"
    else
        echo "  - No hay contenedores ejecutándose"
    fi
    
    echo ""
    print_status "🌐 Estado de la aplicación:"
    if curl -f http://localhost:3600/actuator/health > /dev/null 2>&1; then
        echo "  - Aplicación: ✅ Funcionando"
        echo "  - Health check: ✅ OK"
    else
        echo "  - Aplicación: ❌ No responde"
    fi
    
    echo ""
    print_status "🗄️  Estado de RDS:"
    RDS_HOST="minimarket.ckbesa4wqwo4.us-east-1.rds.amazonaws.com"
    if nc -z $RDS_HOST 3306 2>/dev/null; then
        echo "  - RDS: ✅ Accesible"
    else
        echo "  - RDS: ❌ No accesible"
    fi
    
    echo ""
    print_status "🔒 Estado de Nginx:"
    if systemctl is-active --quiet nginx; then
        echo "  - Nginx: ✅ Activo"
    else
        echo "  - Nginx: ❌ Inactivo"
    fi
}

# Función para mostrar logs
show_logs() {
    print_header "Logs de la aplicación"
    
    echo ""
    print_status "📋 Últimos logs de la aplicación:"
    docker-compose logs --tail=50 desarrolloweb
    
    echo ""
    print_status "📋 Logs de Nginx:"
    if [ -f "/var/log/nginx/desarrolloweb_access.log" ]; then
        sudo tail -20 /var/log/nginx/desarrolloweb_access.log
    else
        echo "  - Log de acceso no encontrado"
    fi
    
    if [ -f "/var/log/nginx/desarrolloweb_error.log" ]; then
        sudo tail -20 /var/log/nginx/desarrolloweb_error.log
    else
        echo "  - Log de errores no encontrado"
    fi
}

# Función para reiniciar la aplicación
restart_app() {
    print_header "Reiniciando la aplicación"
    
    print_status "Deteniendo contenedores..."
    docker-compose down
    
    print_status "Limpiando recursos..."
    docker system prune -f
    
    print_status "Levantando contenedores..."
    docker-compose up -d
    
    print_status "Esperando a que la aplicación esté lista..."
    sleep 30
    
    if curl -f http://localhost:3600/actuator/health > /dev/null 2>&1; then
        print_status "✅ Aplicación reiniciada correctamente"
    else
        print_error "❌ Error al reiniciar la aplicación"
        docker-compose logs desarrolloweb
    fi
}

# Función para limpieza
cleanup() {
    print_header "Limpiando recursos del sistema"
    
    print_status "Limpiando contenedores detenidos..."
    docker container prune -f
    
    print_status "Limpiando imágenes no utilizadas..."
    docker image prune -f
    
    print_status "Limpiando volúmenes no utilizados..."
    docker volume prune -f
    
    print_status "Limpiando redes no utilizadas..."
    docker network prune -f
    
    print_status "Limpiando logs de Nginx..."
    if [ -f "/var/log/nginx/desarrolloweb_access.log" ]; then
        sudo truncate -s 0 /var/log/nginx/desarrolloweb_access.log
    fi
    if [ -f "/var/log/nginx/desarrolloweb_error.log" ]; then
        sudo truncate -s 0 /var/log/nginx/desarrolloweb_error.log
    fi
    
    print_status "✅ Limpieza completada"
}

# Función para verificar RDS
check_rds() {
    print_header "Verificando conectividad con RDS"
    
    if [ -f "check-rds.sh" ]; then
        chmod +x check-rds.sh
        ./check-rds.sh
    else
        print_error "Script check-rds.sh no encontrado"
    fi
}

# Función para mostrar ayuda
show_help() {
    echo "Uso: $0 [comando]"
    echo ""
    echo "Comandos disponibles:"
    echo "  status    - Mostrar estado del sistema y aplicación"
    echo "  logs      - Mostrar logs de la aplicación y Nginx"
    echo "  restart   - Reiniciar la aplicación"
    echo "  cleanup   - Limpiar recursos del sistema"
    echo "  rds       - Verificar conectividad con RDS"
    echo "  help      - Mostrar esta ayuda"
    echo ""
    echo "Ejemplos:"
    echo "  $0 status"
    echo "  $0 logs"
    echo "  $0 restart"
    echo "  $0 rds"
}

# Función principal
main() {
    case "${1:-status}" in
        status)
            show_status
            ;;
        logs)
            show_logs
            ;;
        restart)
            restart_app
            ;;
        cleanup)
            cleanup
            ;;
        rds)
            check_rds
            ;;
        help|--help|-h)
            show_help
            ;;
        *)
            print_error "Comando desconocido: $1"
            show_help
            exit 1
            ;;
    esac
}

# Ejecutar función principal
main "$@" 