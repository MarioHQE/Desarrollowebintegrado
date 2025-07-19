#!/bin/bash

# Script para generar certificados SSL de forma segura
# NO subir certificados a GitHub

set -e

echo "🔐 Generando certificado SSL..."

# Variables
CERT_DIR="ssl"
CERT_NAME="desarrolloweb-ssl"
KEYSTORE_PASSWORD="password123"

# Crear directorio de certificados
mkdir -p $CERT_DIR

# Generar certificado autofirmado
echo "📜 Generando certificado autofirmado..."
keytool -genkeypair \
    -alias $CERT_NAME \
    -keyalg RSA \
    -keysize 2048 \
    -storetype PKCS12 \
    -keystore $CERT_DIR/$CERT_NAME.p12 \
    -validity 365 \
    -storepass $KEYSTORE_PASSWORD \
    -dname "CN=localhost, OU=Development, O=Desarrolloweb, L=Local, ST=Local, C=PE"

# Copiar certificado a recursos de Spring Boot
echo "📁 Copiando certificado a recursos..."
cp $CERT_DIR/$CERT_NAME.p12 desarrolloweb/src/main/resources/

# Generar archivo de configuración de ejemplo
echo "📝 Generando archivo de configuración de ejemplo..."
cat > $CERT_DIR/ssl-config.example.properties << EOF
# Configuración SSL de ejemplo
# Copiar este archivo a application-prod.properties y ajustar valores

server.ssl.enabled=true
server.ssl.key-store=classpath:$CERT_NAME.p12
server.ssl.key-store-password=$KEYSTORE_PASSWORD
server.ssl.key-store-type=PKCS12
server.ssl.key-alias=$CERT_NAME
server.ssl.protocol=TLS
EOF

echo "✅ Certificado SSL generado exitosamente!"
echo "📁 Ubicación: $CERT_DIR/$CERT_NAME.p12"
echo "🔑 Contraseña: $KEYSTORE_PASSWORD"
echo ""
echo "⚠️  IMPORTANTE:"
echo "   - NO subir el archivo .p12 a GitHub"
echo "   - El archivo está en .gitignore"
echo "   - Para producción, usar Let's Encrypt"
echo ""
echo "📋 Para usar en EC2:"
echo "   1. Copiar el certificado a la instancia EC2"
echo "   2. Usar el script deploy-ec2-selfsigned.sh" 