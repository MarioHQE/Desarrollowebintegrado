#!/bin/bash

# Script de monitoreo para la aplicación
# Uso: ./monitor.sh [status|logs|restart|cleanup]

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
    docker-compose -f docker-compose.prod.yml logs --tail=50 desarrolloweb
    
    echo ""
    print_status "📋 Logs de Nginx:"
    sudo tail -20 /var/log/nginx/desarrolloweb_access.log
    sudo tail -20 /var/log/nginx/desarrolloweb_error.log
}

# Función para reiniciar la aplicación
restart_app() {
    print_header "Reiniciando la aplicación"
    
    print_status "Deteniendo contenedores..."
    docker-compose -f docker-compose.prod.yml down
    
    print_status "Limpiando recursos..."
    docker system prune -f
    
    print_status "Levantando contenedores..."
    docker-compose -f docker-compose.prod.yml up -d
    
    print_status "Esperando a que la aplicación esté lista..."
    sleep 30
    
    if curl -f http://localhost:3600/actuator/health > /dev/null 2>&1; then
        print_status "✅ Aplicación reiniciada correctamente"
    else
        print_error "❌ Error al reiniciar la aplicación"
        docker-compose -f docker-compose.prod.yml logs desarrolloweb
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
    sudo truncate -s 0 /var/log/nginx/desarrolloweb_access.log
    sudo truncate -s 0 /var/log/nginx/desarrolloweb_error.log
    
    print_status "✅ Limpieza completada"
}

# Función para backup
backup() {
    print_header "Creando backup de la aplicación"
    
    BACKUP_DIR="/opt/backups/$(date +%Y%m%d_%H%M%S)"
    sudo mkdir -p $BACKUP_DIR
    
    print_status "Backup de logs..."
    sudo cp -r /var/log/nginx/ $BACKUP_DIR/
    
    print_status "Backup de configuración..."
    sudo cp docker-compose.prod.yml $BACKUP_DIR/
    sudo cp env.prod $BACKUP_DIR/
    sudo cp nginx-app.conf $BACKUP_DIR/
    
    print_status "Backup de base de datos (si es local)..."
    if docker ps | grep -q mysqldb; then
        docker exec mysqldb mysqldump -u root -pmario14y15. cremeria > $BACKUP_DIR/database_backup.sql
    fi
    
    print_status "✅ Backup creado en: $BACKUP_DIR"
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
    echo "  backup    - Crear backup de la aplicación"
    echo "  help      - Mostrar esta ayuda"
    echo ""
    echo "Ejemplos:"
    echo "  $0 status"
    echo "  $0 logs"
    echo "  $0 restart"
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
        backup)
            backup
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