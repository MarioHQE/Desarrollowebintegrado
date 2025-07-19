# 🚀 Guía de Despliegue en EC2 con Docker

Esta guía te ayudará a desplegar tu aplicación Spring Boot en un servidor EC2 de AWS usando Docker y conectándola a tu base de datos RDS.

## 📋 Prerrequisitos

- Una instancia EC2 con Ubuntu 20.04 o superior
- Acceso SSH a la instancia EC2
- Base de datos RDS MySQL configurada
- Grupo de seguridad configurado para permitir tráfico en el puerto 3600

## 🔧 Configuración de la Instancia EC2

### 1. Conectar a tu instancia EC2

```bash
ssh -i tu-key.pem ubuntu@tu-ip-ec2
```

### 2. Actualizar el sistema

```bash
sudo apt update && sudo apt upgrade -y
```

### 3. Configurar el grupo de seguridad

Asegúrate de que tu grupo de seguridad permita:
- Puerto 22 (SSH)
- Puerto 3600 (Aplicación)
- Puerto 80 (HTTP, opcional para proxy reverso)

## 📦 Instalación y Despliegue

### Opción 1: Despliegue Automatizado (Recomendado)

1. **Subir archivos al servidor**

```bash
# Desde tu máquina local
scp -i tu-key.pem -r desarrolloweb/ ubuntu@tu-ip-ec2:~/
scp -i tu-key.pem docker-compose.prod.yml ubuntu@tu-ip-ec2:~/
scp -i tu-key.pem env.prod ubuntu@tu-ip-ec2:~/
scp -i tu-key.pem deploy.sh ubuntu@tu-ip-ec2:~/
```

2. **Ejecutar el script de despliegue**

```bash
# En la instancia EC2
chmod +x deploy.sh
./deploy.sh
```

### Opción 2: Despliegue Manual

1. **Instalar Docker**

```bash
curl -fsSL https://get.docker.com -o get-docker.sh
sh get-docker.sh
sudo usermod -aG docker $USER
rm get-docker.sh
```

2. **Instalar Docker Compose**

```bash
sudo curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose
```

3. **Configurar variables de entorno**

Edita el archivo `env.prod` con tus credenciales:

```bash
nano env.prod
```

4. **Desplegar la aplicación**

```bash
docker-compose -f docker-compose.prod.yml up -d --build
```

## 🔍 Verificación del Despliegue

### Verificar que la aplicación esté funcionando

```bash
# Verificar logs
docker-compose -f docker-compose.prod.yml logs -f desarrolloweb

# Verificar health check
curl http://localhost:3600/actuator/health

# Verificar Swagger UI
curl http://localhost:3600/swagger-ui.html
```

### Verificar contenedores

```bash
docker ps
docker-compose -f docker-compose.prod.yml ps
```

## 🌐 Acceso a la Aplicación

Una vez desplegada, tu aplicación estará disponible en:

- **API Principal**: `http://tu-ip-ec2:3600`
- **Swagger UI**: `http://tu-ip-ec2:3600/swagger-ui.html`
- **Health Check**: `http://tu-ip-ec2:3600/actuator/health`

## 🔧 Comandos Útiles

### Gestión de la aplicación

```bash
# Ver logs en tiempo real
docker-compose -f docker-compose.prod.yml logs -f desarrolloweb

# Reiniciar la aplicación
docker-compose -f docker-compose.prod.yml restart desarrolloweb

# Detener la aplicación
docker-compose -f docker-compose.prod.yml down

# Ver estadísticas de recursos
docker stats
```

### Limpieza del sistema

```bash
# Limpiar contenedores no utilizados
docker container prune

# Limpiar imágenes no utilizadas
docker image prune

# Limpieza completa
docker system prune -a
```

## 🔒 Configuración de Seguridad

### 1. Configurar firewall (UFW)

```bash
sudo ufw allow 22/tcp
sudo ufw allow 3600/tcp
sudo ufw enable
```

### 2. Configurar variables de entorno seguras

Considera usar AWS Secrets Manager o Parameter Store para las credenciales:

```bash
# Ejemplo con AWS CLI
aws secretsmanager create-secret --name "desarrolloweb/db-credentials" --secret-string '{"username":"root","password":"tu-password"}'
```

### 3. Configurar SSL/TLS (Opcional)

Para producción, considera usar un proxy reverso con Nginx y Let's Encrypt:

```bash
# Instalar Nginx
sudo apt install nginx

# Configurar proxy reverso
sudo nano /etc/nginx/sites-available/desarrolloweb
```

## 📊 Monitoreo

### Configurar monitoreo básico

```bash
# Instalar herramientas de monitoreo
sudo apt install htop iotop

# Ver uso de recursos
htop
```

### Logs del sistema

```bash
# Ver logs del sistema
sudo journalctl -u docker.service

# Ver logs de la aplicación
docker-compose -f docker-compose.prod.yml logs desarrolloweb
```

## 🚨 Solución de Problemas

### Problema: La aplicación no inicia

```bash
# Verificar logs detallados
docker-compose -f docker-compose.prod.yml logs desarrolloweb

# Verificar conectividad con RDS
telnet minimarket.ckbesa4wqwo4.us-east-1.rds.amazonaws.com 3306
```

### Problema: Error de conexión a la base de datos

1. Verificar que el grupo de seguridad de RDS permita conexiones desde tu EC2
2. Verificar las credenciales en `env.prod`
3. Verificar que la base de datos esté activa

### Problema: Puerto ocupado

```bash
# Verificar qué está usando el puerto 3600
sudo netstat -tulpn | grep :3600

# Matar el proceso si es necesario
sudo kill -9 <PID>
```

## 🔄 Actualizaciones

Para actualizar la aplicación:

```bash
# Detener la aplicación
docker-compose -f docker-compose.prod.yml down

# Actualizar código (si es necesario)
git pull origin main

# Reconstruir y levantar
docker-compose -f docker-compose.prod.yml up -d --build
```

## 📞 Soporte

Si encuentras problemas:

1. Revisa los logs: `docker-compose -f docker-compose.prod.yml logs desarrolloweb`
2. Verifica la conectividad de red
3. Verifica las credenciales de la base de datos
4. Revisa la configuración del grupo de seguridad de AWS

---

**Nota**: Recuerda cambiar las credenciales por defecto en producción y usar variables de entorno seguras. 