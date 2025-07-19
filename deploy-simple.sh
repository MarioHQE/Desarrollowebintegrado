#!/bin/bash

# Configuración
PROJECT_ID="restaurante-diana"
REGION="us-central1"
SERVICE_NAME="desarrolloweb-service"

echo "🚀 Desplegando en Google Cloud Run (versión simplificada)..."

# Verificar que gcloud está instalado
if ! command -v gcloud &> /dev/null; then
    echo "❌ Error: gcloud CLI no está instalado"
    exit 1
fi

# Configurar proyecto
echo "⚙️ Configurando proyecto: $PROJECT_ID"
gcloud config set project $PROJECT_ID

# Construir y subir la imagen
echo "🔨 Construyendo imagen con Cloud Build..."
gcloud builds submit --tag gcr.io/$PROJECT_ID/desarrolloweb

# Desplegar en Cloud Run con configuración mínima
echo "🚀 Desplegando en Cloud Run..."
gcloud run deploy $SERVICE_NAME \
  --image gcr.io/$PROJECT_ID/desarrolloweb \
  --platform managed \
  --region $REGION \
  --allow-unauthenticated \
  --port 8080 \
  --memory 512Mi \
  --cpu 1 \
  --max-instances 10 \
  --timeout 300 \
  --set-env-vars "SPRING_PROFILES_ACTIVE=prod" \
  --set-env-vars "PORT=8080" \
  --set-env-vars "DATABASE_URL=jdbc:mysql://minimarket.ckbesa4wqwo4.us-east-1.rds.amazonaws.com:3306/minimarket" \
  --set-env-vars "DATABASE_USERNAME=adminsq" \
  --set-env-vars "DATABASE_PASSWORD=Mario14y15." \
  --set-env-vars "AWS_ACCESS_KEY=AKIAWYOPDF3QT7NDAJUC" \
  --set-env-vars "AWS_SECRET_KEY=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" \
  --set-env-vars "STRIPE_SECRET_KEY=sk_test_51RRpwwRS5ApzbFdePxJPtFPIU5SoWXi6a9r3BfW92TNd6cPSTMf3n3BfnazI9t0KBir2VbNPYLifXVjSVUmPrlPX001PqO7jS7" \
  --set-env-vars "STRIPE_WEBHOOK_SECRET=whsec_9850a314cfa962c23f3616368e571af42d3d78f675cac0825c67d2d5bdffbee5" \
  --set-env-vars "MAIL_HOST=smtp.gmail.com" \
  --set-env-vars "MAIL_PORT=587" \
  --set-env-vars "MAIL_USERNAME=marioelpro08@gmail.com" \
  --set-env-vars "MAIL_PASSWORD=bmch rbeg tfmx ondv"

# Obtener la URL del servicio
echo "✅ Despliegue completado!"
SERVICE_URL=$(gcloud run services describe $SERVICE_NAME --region=$REGION --format="value(status.url)")
echo "🌐 URL del servicio: $SERVICE_URL"
echo "🏥 Health Check: $SERVICE_URL/health"
echo "📊 Swagger: $SERVICE_URL/swagger-ui.html"

# Mostrar información del servicio
echo "📋 Información del servicio:"
gcloud run services describe $SERVICE_NAME --region=$REGION --format="table(metadata.name,status.url,spec.template.spec.containers[0].image)" 