# 🚀 Guía de Despliegue en AWS EC2

Esta guía te ayudará a desplegar tu aplicación Spring Boot en AWS EC2 usando Docker.

## 📋 Prerrequisitos

- Cuenta de AWS con acceso a EC2
- Clave SSH para conectarte a la instancia
- Base de datos RDS configurada (opcional, puedes usar MySQL local)
- Dominio configurado (opcional)

## 🏗️ Paso 1: Crear Instancia EC2

### 1.1 Configuración de la Instancia

1. Ve a la consola de AWS EC2
2. Haz clic en "Launch Instance"
3. Configura la instancia:
   - **Name**: `desarrolloweb-prod`
   - **AMI**: Ubuntu Server 22.04 LTS
   - **Instance Type**: t3.medium (mínimo recomendado)
   - **Key Pair**: Selecciona tu clave SSH existente o crea una nueva
   - **Security Group**: Crea uno nuevo con las siguientes reglas:
     - SSH (22) - Tu IP
     - HTTP (80) - 0.0.0.0/0
     - HTTPS (443) - 0.0.0.0/0
     - Custom TCP (3600) - 0.0.0.0/0 (puerto de la aplicación)

### 1.2 Configuración de Storage

- **Root Volume**: 20 GB (mínimo)
- **Volume Type**: gp3
- **Delete on Termination**: No (recomendado)

## 🔧 Paso 2: Configuración Inicial de la Instancia

### 2.1 Conectarse a la Instancia

```bash
ssh -i tu-clave.pem ubuntu@tu-ip-ec2
```

### 2.2 Ejecutar Script de Configuración

```bash
# Descargar el script de configuración
wget https://raw.githubusercontent.com/tu-usuario/tu-repo/main/setup-ec2.sh
chmod +x setup-ec2.sh
./setup-ec2.sh
```

**O si ya tienes el archivo en tu proyecto:**

```bash
# Subir archivos al servidor
scp -i tu-clave.pem -r . ubuntu@tu-ip-ec2:/opt/desarrolloweb/

# Conectarse y ejecutar
ssh -i tu-clave.pem ubuntu@tu-ip-ec2
cd /opt/desarrolloweb
chmod +x setup-ec2.sh
./setup-ec2.sh
```

### 2.3 Reiniciar Sesión SSH

```bash
# Desconectarse y volver a conectar para que los cambios de grupo surtan efecto
exit
ssh -i tu-clave.pem ubuntu@tu-ip-ec2
```

## 🗄️ Paso 3: Configurar Base de Datos

### Opción A: Usar RDS (Recomendado para Producción)

1. Crea una instancia RDS MySQL en AWS
2. Configura el Security Group para permitir conexiones desde tu EC2
3. Actualiza las variables de entorno en `env.prod`:

```bash
# Editar el archivo env.prod
nano env.prod

# Actualizar la URL de la base de datos
DATABASE_URL=jdbc:mysql://tu-instancia-rds.region.rds.amazonaws.com:3306/cremeria?createDatabaseIfNotExist=true&useSSL=true&requireSSL=false&allowPublicKeyRetrieval=true
```

### Opción B: Usar MySQL Local (Docker)

Si prefieres usar MySQL local, modifica `docker-compose.prod.yml` para incluir MySQL:

```yaml
services:
  desarrolloweb:
    # ... configuración existente ...
    depends_on:
      - mysqldb

  mysqldb:
    container_name: mysqldb
    image: mysql:latest
    ports:
      - "3307:3306"
    environment:
      MYSQL_DATABASE: cremeria
      MYSQL_PASSWORD: mario14y15.
      MYSQL_ROOT_PASSWORD: mario14y15.
    volumes:
      - mysql_data:/var/lib/mysql

volumes:
  mysql_data:
```

## 🔐 Paso 4: Configurar Variables de Entorno

### 4.1 Actualizar Variables de Entorno

Edita el archivo `env.prod` con tus credenciales reales:

```bash
# Configuración de Base de Datos RDS
DB_USERNAME=tu_usuario
DB_PASSWORD=tu_password_seguro

# Configuración de AWS (crear usuario IAM con permisos mínimos)
AWS_ACCESS_KEY=tu_access_key
AWS_SECRET_KEY=tu_secret_key

# Configuración de Stripe
STRIPE_SECRET_KEY=tu_stripe_secret_key
STRIPE_WEBHOOK_SECRET=tu_webhook_secret

# Configuración de Email
MAIL_USERNAME=tu_email@gmail.com
MAIL_PASSWORD=tu_app_password
```

### 4.2 Configurar Usuario IAM para AWS

1. Ve a IAM en AWS Console
2. Crea un nuevo usuario
3. Asigna la política `AmazonS3FullAccess` (o una más restrictiva)
4. Genera Access Key y Secret Key
5. Actualiza `env.prod` con las nuevas credenciales

## 🚀 Paso 5: Desplegar la Aplicación

### 5.1 Ejecutar Script de Despliegue

```bash
cd /opt/desarrolloweb
chmod +x deploy.sh
./deploy.sh
```

### 5.2 Verificar el Despliegue

```bash
# Verificar estado
./monitor.sh status

# Ver logs
./monitor.sh logs

# Verificar que la aplicación responde
curl http://localhost:3600/actuator/health
```

## 🌐 Paso 6: Configurar Dominio (Opcional)

### 6.1 Configurar DNS

1. Ve a tu proveedor de DNS
2. Crea un registro A apuntando a la IP pública de tu EC2
3. Ejemplo: `api.tudominio.com` → `IP_DE_TU_EC2`

### 6.2 Actualizar Configuración de Nginx

Edita `nginx-app.conf`:

```nginx
server {
    listen 80;
    server_name api.tudominio.com;  # Cambiar por tu dominio
    
    # ... resto de la configuración ...
}
```

### 6.3 Configurar SSL con Let's Encrypt

```bash
# Instalar Certbot
sudo apt-get install certbot python3-certbot-nginx

# Obtener certificado SSL
sudo certbot --nginx -d api.tudominio.com

# Configurar renovación automática
sudo crontab -e
# Agregar: 0 12 * * * /usr/bin/certbot renew --quiet
```

## 📊 Paso 7: Monitoreo y Mantenimiento

### 7.1 Comandos de Monitoreo

```bash
# Ver estado del sistema
./monitor.sh status

# Ver logs en tiempo real
./monitor.sh logs

# Reiniciar aplicación
./monitor.sh restart

# Limpiar recursos
./monitor.sh cleanup

# Crear backup
./monitor.sh backup
```

### 7.2 Configurar Monitoreo Automático

Crear un cron job para monitoreo:

```bash
# Editar crontab
crontab -e

# Agregar tareas de monitoreo
# Verificar estado cada 5 minutos
*/5 * * * * /opt/desarrolloweb/monitor.sh status > /dev/null 2>&1

# Limpiar logs diariamente a las 2 AM
0 2 * * * /opt/desarrolloweb/monitor.sh cleanup > /dev/null 2>&1

# Backup semanal los domingos a las 3 AM
0 3 * * 0 /opt/desarrolloweb/monitor.sh backup > /dev/null 2>&1
```

## 🔄 Paso 8: Actualizaciones

### 8.1 Proceso de Actualización

```bash
# 1. Hacer backup
./monitor.sh backup

# 2. Detener aplicación
docker-compose -f docker-compose.prod.yml down

# 3. Actualizar código
git pull origin main

# 4. Reconstruir y levantar
docker-compose -f docker-compose.prod.yml up -d --build

# 5. Verificar
./monitor.sh status
```

### 8.2 Rollback

```bash
# Si algo sale mal, hacer rollback
docker-compose -f docker-compose.prod.yml down
git checkout HEAD~1
docker-compose -f docker-compose.prod.yml up -d --build
```

## 🛠️ Solución de Problemas

### Problemas Comunes

1. **Aplicación no responde**
   ```bash
   ./monitor.sh logs
   docker-compose -f docker-compose.prod.yml logs desarrolloweb
   ```

2. **Error de conexión a base de datos**
   - Verificar Security Groups de RDS
   - Verificar credenciales en `env.prod`
   - Verificar conectividad: `telnet tu-rds-endpoint 3306`

3. **Error de memoria**
   ```bash
   # Aumentar memoria JVM en docker-compose.prod.yml
   environment:
     - JAVA_OPTS=-Xmx1g -Xms512m
   ```

4. **Nginx no funciona**
   ```bash
   sudo nginx -t
   sudo systemctl status nginx
   sudo journalctl -u nginx
   ```

### Logs Importantes

- **Aplicación**: `docker-compose -f docker-compose.prod.yml logs desarrolloweb`
- **Nginx**: `/var/log/nginx/desarrolloweb_access.log`
- **Sistema**: `journalctl -u docker`

## 📈 Optimización

### 1. Configuración de JVM

Ajusta `JAVA_OPTS` según los recursos de tu instancia:

```bash
# Para t3.medium (2 vCPU, 4 GB RAM)
JAVA_OPTS="-Xmx2g -Xms1g -XX:+UseG1GC"

# Para t3.small (2 vCPU, 2 GB RAM)
JAVA_OPTS="-Xmx1g -Xms512m -XX:+UseG1GC"
```

### 2. Configuración de Nginx

Optimiza `nginx-app.conf` para tu caso de uso:

```nginx
# Aumentar buffer sizes para archivos grandes
client_max_body_size 50M;
proxy_buffer_size 128k;
proxy_buffers 4 256k;
proxy_busy_buffers_size 256k;
```

### 3. Monitoreo de Recursos

```bash
# Instalar herramientas adicionales
sudo apt-get install -y htop iotop nethogs

# Monitorear en tiempo real
htop
iotop
nethogs
```

## 🔒 Seguridad

### 1. Firewall

```bash
# Verificar reglas de firewall
sudo ufw status

# Solo permitir IPs específicas para SSH
sudo ufw allow from tu-ip/32 to any port 22
```

### 2. Actualizaciones de Seguridad

```bash
# Configurar actualizaciones automáticas
sudo apt-get install unattended-upgrades
sudo dpkg-reconfigure -plow unattended-upgrades
```

### 3. Rotación de Logs

```bash
# Configurar rotación de logs
sudo logrotate -f /etc/logrotate.d/docker-container
```

## 📞 Soporte

Si encuentras problemas:

1. Revisa los logs: `./monitor.sh logs`
2. Verifica el estado: `./monitor.sh status`
3. Consulta la documentación de Spring Boot y Docker
4. Revisa los logs de AWS CloudWatch (si configurado)

---

**¡Tu aplicación Spring Boot está lista para producción en AWS EC2! 🎉** 