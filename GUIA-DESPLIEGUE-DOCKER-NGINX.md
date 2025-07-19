# 🚀 Guía de Despliegue con Docker y Nginx en EC2

Esta guía te ayudará a desplegar tu aplicación Spring Boot en EC2 usando Docker y Nginx como proxy reverso.

## 📋 Prerrequisitos

- ✅ Instancia EC2 funcionando
- ✅ Acceso SSH a la instancia
- ✅ Proyecto Spring Boot listo
- ✅ Base de datos RDS configurada

## 🏗️ Arquitectura del Despliegue

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Cliente Web   │    │   Nginx (80)    │    │   Spring Boot   │
│                 │◄──►│   Proxy Reverso │◄──►│   Docker (3600) │
│                 │    │                 │    │                 │
└─────────────────┘    └─────────────────┘    └─────────────────┘
                              │
                              ▼
                       ┌─────────────────┐
                       │   RDS MySQL     │
                       │   (Base de      │
                       │    Datos)       │
                       └─────────────────┘
```

## 🔧 Paso 1: Preparar la Instancia EC2

### 1.1 Conectarse a la instancia

```bash
ssh -i "tu-clave.pem" ec2-user@tu-ip-publica
```

### 1.2 Actualizar el sistema

```bash
sudo yum update -y
```

## 🐳 Paso 2: Instalar Docker y Docker Compose

### 2.1 Instalar Docker

```bash
# Instalar Docker
sudo yum install -y docker

# Iniciar y habilitar Docker
sudo systemctl start docker
sudo systemctl enable docker

# Agregar usuario al grupo docker
sudo usermod -a -G docker ec2-user

# Verificar instalación
docker --version
```

### 2.2 Instalar Docker Compose

```bash
# Descargar Docker Compose
sudo curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose

# Dar permisos de ejecución
sudo chmod +x /usr/local/bin/docker-compose

# Verificar instalación
docker-compose --version
```

### 2.3 Reiniciar sesión SSH

```bash
exit
ssh -i "tu-clave.pem" ec2-user@tu-ip-publica
```

## 📦 Paso 3: Subir el Código a la Instancia

### 3.1 Opción A: Usando SCP (Recomendado)

```bash
# Desde tu máquina local, crear archivo comprimido
tar -czf desarrolloweb.tar.gz desarrolloweb/ docker-compose-nginx.yml nginx.conf

# Subir a la instancia
scp -i "tu-clave.pem" desarrolloweb.tar.gz ec2-user@tu-ip-publica:~/

# En la instancia, extraer
ssh -i "tu-clave.pem" ec2-user@tu-ip-publica
cd ~
tar -xzf desarrolloweb.tar.gz
```

### 3.2 Opción B: Usando Git

```bash
# En la instancia EC2
cd ~
git clone https://github.com/tu-usuario/tu-repositorio.git
cd tu-repositorio
```

## 🚀 Paso 4: Desplegar con Docker y Nginx

### 4.1 Usar el script automatizado

```bash
# Dar permisos de ejecución
chmod +x deploy-docker-nginx.sh

# Ejecutar el script
./deploy-docker-nginx.sh
```

### 4.2 Desplegar manualmente

```bash
# 1. Construir la imagen Docker
cd desarrolloweb
docker build -t desarrolloweb-app .
cd ..

# 2. Desplegar con Docker Compose
docker-compose -f docker-compose-nginx.yml up -d

# 3. Verificar que todo esté funcionando
docker ps
docker logs desarrolloweb-container
```

## 🔍 Paso 5: Verificar el Despliegue

### 5.1 Verificar contenedores

```bash
# Ver contenedores en ejecución
docker ps

# Ver logs de la aplicación
docker logs desarrolloweb-container -f

# Ver logs de Nginx
docker logs nginx-proxy -f
```

### 5.2 Verificar conectividad

```bash
# Verificar aplicación directamente
curl http://localhost:3600/actuator/health

# Verificar a través de Nginx
curl http://localhost/health

# Verificar desde el exterior
curl http://tu-ip-publica/health
```

### 5.3 Verificar puertos

```bash
# Ver puertos en uso
netstat -tlnp | grep -E ':(80|3600)'

# Verificar firewall
sudo firewall-cmd --list-ports
```

## 🌐 Paso 6: Acceder a la Aplicación

### 6.1 URLs de acceso

```bash
# Obtener IP pública
curl http://169.254.169.254/latest/meta-data/public-ipv4

# URLs disponibles:
# - Aplicación: http://TU-IP-PUBLICA/
# - Health check: http://TU-IP-PUBLICA/health
# - Swagger UI: http://TU-IP-PUBLICA/swagger-ui/
# - API Docs: http://TU-IP-PUBLICA/v3/api-docs/
```

## 🔧 Paso 7: Configuración de SSL (Opcional)

### 7.1 Instalar Certbot

```bash
# Instalar Certbot
sudo yum install -y certbot python3-certbot-nginx

# Obtener certificado SSL
sudo certbot --nginx -d tu-dominio.com --non-interactive --agree-tos --email tu-email@example.com

# Configurar renovación automática
(crontab -l 2>/dev/null; echo "0 12 * * * /usr/bin/certbot renew --quiet") | crontab -
```

### 7.2 Configurar Nginx con SSL

Editar el archivo `nginx.conf` y descomentar la sección HTTPS:

```nginx
# Descomentar y configurar la sección del servidor HTTPS
server {
    listen 443 ssl http2;
    server_name tu-dominio.com;
    
    ssl_certificate /etc/nginx/ssl/cert.pem;
    ssl_certificate_key /etc/nginx/ssl/key.pem;
    # ... resto de configuración SSL
}
```

## 🛠️ Comandos Útiles

### Gestión de Contenedores

```bash
# Ver contenedores
docker ps

# Ver logs
docker logs desarrolloweb-container -f
docker logs nginx-proxy -f

# Reiniciar contenedores
docker restart desarrolloweb-container
docker restart nginx-proxy

# Detener y eliminar contenedores
docker-compose -f docker-compose-nginx.yml down

# Reconstruir y desplegar
docker-compose -f docker-compose-nginx.yml up -d --build
```

### Gestión de Nginx

```bash
# Verificar configuración
docker exec nginx-proxy nginx -t

# Recargar configuración
docker exec nginx-proxy nginx -s reload

# Ver logs de Nginx
docker exec nginx-proxy tail -f /var/log/nginx/desarrolloweb_access.log
```

### Monitoreo

```bash
# Ver estadísticas de contenedores
docker stats

# Ver uso de recursos
htop
free -h
df -h

# Ver logs del sistema
sudo journalctl -f
```

## 🔒 Configuración de Seguridad

### 1. Configurar Security Groups

Asegúrate de que tu Security Group permita:
- Puerto 22 (SSH)
- Puerto 80 (HTTP)
- Puerto 443 (HTTPS) - si usas SSL

### 2. Configurar Firewall

```bash
# Verificar firewall
sudo firewall-cmd --list-all

# Agregar servicios si es necesario
sudo firewall-cmd --permanent --add-service=http
sudo firewall-cmd --permanent --add-service=https
sudo firewall-cmd --reload
```

### 3. Actualizar Sistema Regularmente

```bash
# Configurar actualizaciones automáticas
sudo yum install -y yum-cron
sudo systemctl enable yum-cron
sudo systemctl start yum-cron
```

## 🚨 Troubleshooting

### Problemas Comunes

#### 1. La aplicación no inicia

```bash
# Verificar logs
docker logs desarrolloweb-container

# Verificar configuración de base de datos
docker exec desarrolloweb-container env | grep DATABASE

# Verificar puerto
netstat -tlnp | grep 3600
```

#### 2. Nginx no puede conectarse a la aplicación

```bash
# Verificar que la aplicación esté corriendo
docker ps | grep desarrolloweb

# Verificar conectividad interna
docker exec nginx-proxy curl http://desarrolloweb:3600/actuator/health

# Verificar logs de Nginx
docker logs nginx-proxy
```

#### 3. Problemas de puerto

```bash
# Verificar puertos en uso
netstat -tlnp

# Verificar firewall
sudo firewall-cmd --list-ports

# Verificar Security Groups en AWS
```

#### 4. Problemas de memoria

```bash
# Ver uso de memoria
free -h

# Ver estadísticas de Docker
docker stats

# Ajustar límites de memoria en docker-compose-nginx.yml
```

## 📊 Monitoreo y Logs

### Logs de la Aplicación

```bash
# Ver logs en tiempo real
docker logs desarrolloweb-container -f

# Ver últimos 100 líneas
docker logs desarrolloweb-container --tail 100

# Ver logs desde una fecha específica
docker logs desarrolloweb-container --since "2024-01-01T00:00:00"
```

### Logs de Nginx

```bash
# Ver logs de acceso
docker exec nginx-proxy tail -f /var/log/nginx/desarrolloweb_access.log

# Ver logs de error
docker exec nginx-proxy tail -f /var/log/nginx/desarrolloweb_error.log

# Ver logs generales
docker logs nginx-proxy -f
```

## 🔄 Actualizaciones

### Actualizar la Aplicación

```bash
# Detener contenedores
docker-compose -f docker-compose-nginx.yml down

# Reconstruir imagen
cd desarrolloweb
docker build -t desarrolloweb-app .
cd ..

# Redesplegar
docker-compose -f docker-compose-nginx.yml up -d

# Verificar
docker ps
curl http://localhost/health
```

### Actualizar Configuración de Nginx

```bash
# Editar configuración
vim nginx.conf

# Reconstruir y redesplegar
docker-compose -f docker-compose-nginx.yml up -d --build

# Verificar configuración
docker exec nginx-proxy nginx -t
```

## 📞 Soporte

Si encuentras problemas:

1. **Verifica los logs:** `docker logs desarrolloweb-container`
2. **Revisa la conectividad:** `curl http://localhost/health`
3. **Verifica recursos:** `docker stats`
4. **Consulta la documentación de Spring Boot**
5. **Revisa los logs de Nginx:** `docker logs nginx-proxy`

## 🎯 Próximos Pasos

1. **Configurar monitoreo** con Prometheus y Grafana
2. **Implementar backup automático** de la base de datos
3. **Configurar CI/CD** con GitHub Actions
4. **Implementar load balancing** con múltiples instancias
5. **Configurar CDN** para mejorar el rendimiento
6. **Implementar rate limiting** más avanzado

---

**¡Tu aplicación Spring Boot está desplegada con Docker y Nginx en EC2! 🚀** 