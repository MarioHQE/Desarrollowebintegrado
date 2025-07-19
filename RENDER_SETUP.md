# Configuración de Render para Desarrolloweb

## Problema Resuelto
El error "Communications link failure" se debía a una configuración incorrecta de las variables de entorno y la conexión a la base de datos en Render.

## Pasos para Configurar en Render

### 1. Crear un nuevo servicio web en Render

1. Ve a [Render Dashboard](https://dashboard.render.com/)
2. Haz clic en "New +" y selecciona "Web Service"
3. Conecta tu repositorio de GitHub
4. Selecciona el repositorio `desarrollowebintegrado`

### 2. Configurar el servicio

- **Name**: `desarrolloweb`
- **Runtime**: `Docker`
- **Region**: Cualquiera (recomendado: US East)
- **Branch**: `main` (o tu rama principal)
- **Root Directory**: `desarrolloweb`
- **Dockerfile Path**: `Dockerfile`

### 3. Configurar Variables de Entorno

En la sección "Environment Variables", agrega las siguientes variables:

#### Variables de Base de Datos (OBLIGATORIAS)
```
DATABASE_URL=jdbc:mysql://tu-host-mysql:3306/tu-database
DATABASE_USERNAME=tu-usuario
DATABASE_PASSWORD=tu-password
```

#### Variables del Sistema
```
SPRING_PROFILES_ACTIVE=render
PORT=3600
```

#### Variables de Servicios Externos
```
STRIPE_SECRET_KEY=sk_test_51RRpwwRS5ApzbFdePxJPtFPIU5SoWXi6a9r3BfW92TNd6cPSTMf3n3BfnazI9t0KBir2VbNPYLifXVjSVUmPrlPX001PqO7jS7
STRIPE_WEBHOOK_SECRET=whsec_9850a314cfa962c23f3616368e571af42d3d78f675cac0825c67d2d5bdffbee5
AWS_ACCESS_KEY=AKIAWYOPDF3QT7NDAJUC
AWS_SECRET_KEY=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
MAIL_USERNAME=marioelpro08@gmail.com
MAIL_PASSWORD=bmch rbeg tfmx ondv
```

### 4. Opciones de Base de Datos

#### Opción A: Usar MySQL en Render (Recomendado)
1. Crea un nuevo servicio "MySQL" en Render
2. Usa las credenciales proporcionadas por Render
3. Actualiza las variables `DATABASE_URL`, `DATABASE_USERNAME`, y `DATABASE_PASSWORD`

#### Opción B: Usar tu base de datos RDS actual
Si quieres seguir usando tu base de datos RDS de AWS:
```
DATABASE_URL=jdbc:mysql://minimarket.ckbesa4wqwo4.us-east-1.rds.amazonaws.com:3306/minimarket
DATABASE_USERNAME=adminsq
DATABASE_PASSWORD=Mario14y15.
```

### 5. Configuración de Health Check

En la sección "Health Check Path", agrega:
```
/actuator/health
```

### 6. Desplegar

1. Haz clic en "Create Web Service"
2. Render comenzará a construir y desplegar tu aplicación
3. El proceso puede tomar 5-10 minutos

## Archivos Modificados

1. **`desarrolloweb/src/main/resources/application-render.properties`** - Configuración específica para Render
2. **`desarrolloweb/src/main/resources/application.properties`** - Actualizado para usar perfiles
3. **`render.yaml`** - Configuración de Render (opcional)

## Verificación

Una vez desplegado, puedes verificar que todo funciona:

1. **Health Check**: `https://tu-app.onrender.com/actuator/health`
2. **Swagger UI**: `https://tu-app.onrender.com/swagger-ui.html`
3. **API Endpoints**: `https://tu-app.onrender.com/api/...`

## Troubleshooting

### Si sigues viendo errores de conexión:

1. **Verifica las variables de entorno**: Asegúrate de que `DATABASE_URL`, `DATABASE_USERNAME`, y `DATABASE_PASSWORD` estén correctamente configuradas
2. **Verifica la conectividad**: Si usas RDS, asegúrate de que el Security Group permita conexiones desde Render
3. **Revisa los logs**: En Render Dashboard, ve a la pestaña "Logs" para ver errores detallados

### Si la aplicación no inicia:

1. **Verifica el puerto**: Asegúrate de que `PORT` esté configurado correctamente
2. **Verifica el perfil**: Asegúrate de que `SPRING_PROFILES_ACTIVE=render`
3. **Revisa el Dockerfile**: Asegúrate de que esté en la ubicación correcta

## Notas Importantes

- **Variables sensibles**: Nunca commits credenciales en el código. Usa variables de entorno.
- **Base de datos**: Para producción, considera usar una base de datos gestionada como Render MySQL o AWS RDS.
- **SSL**: Si usas RDS, asegúrate de que la URL incluya `?useSSL=true&requireSSL=false` si es necesario. 