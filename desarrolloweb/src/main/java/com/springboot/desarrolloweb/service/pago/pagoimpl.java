package com.springboot.desarrolloweb.service.pago;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.desarrolloweb.dao.pedidorepository;
import com.springboot.desarrolloweb.dao.productosucursalrepository;
import com.springboot.desarrolloweb.entity.pedido;
import com.springboot.desarrolloweb.service.pedido.pedidoimpl;
import com.stripe.Stripe;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.stripe.param.checkout.SessionCreateParams;
import com.stripe.param.checkout.SessionCreateParams.LineItem;
import com.stripe.param.checkout.SessionCreateParams.Mode;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class pagoimpl implements pagoservice {
        @Value("${stripe.secretkey}")
        String secretkey;

        @Value("${stripe.webhook.secret}")
        String webhookSecret;

        @Autowired
        pedidorepository pedidodao;

        @Autowired
        productosucursalrepository productosucursaldao;

        @Autowired
        pedidoimpl pedidoService;

        @Override
        public ResponseEntity<String> sesiondepago(int id_pedido) throws StripeException, JsonProcessingException {
                pedido pedido = pedidodao.findById(id_pedido)
                                .orElseThrow(() -> new RuntimeException(
                                                "No se encontró el pedido con ID: " + id_pedido));

                // Verificar que el pedido esté en estado PENDIENTE
                if (!"PENDIENTE".equals(pedido.getEstado())) {
                        return ResponseEntity.badRequest()
                                        .body("El pedido no está en estado pendiente. Estado actual: "
                                                        + pedido.getEstado());
                }

                Stripe.apiKey = secretkey;
                List<LineItem> lista = new ArrayList<>();

                pedido.getPedidoProducto().forEach((pp) -> {
                        LineItem lineItem = LineItem.builder()
                                        .setPriceData(LineItem.PriceData.builder()
                                                        .setProductData(LineItem.PriceData.ProductData.builder()
                                                                        .setName(pp.getProductoSucursal().getProducto()
                                                                                        .getNombre())
                                                                        .setDescription(pp.getProductoSucursal()
                                                                                        .getProducto().getDescripcion())
                                                                        .build())
                                                        .setUnitAmount((long) (pp.getProductoSucursal().getProducto()
                                                                        .getPrecio() * 100))
                                                        .setCurrency("PEN")
                                                        .build())
                                        .setQuantity(Long.valueOf(pp.getCantidad()))
                                        .build();
                        lista.add(lineItem);
                });

                SessionCreateParams sessionCreateParams = SessionCreateParams.builder()
                                .setMode(Mode.PAYMENT)
                                .setCustomerEmail(pedido.getUsuario() != null ? pedido.getUsuario().getEmail()
                                                : pedido.getEmail())
                                .setPaymentIntentData(SessionCreateParams.PaymentIntentData.builder()
                                                .putMetadata("pedido_id", String.valueOf(id_pedido))
                                                .build())
                                .setSuccessUrl("http://localhost:5173/pedido/" + id_pedido + "?payment=success")
                                .setCancelUrl("http://localhost:5173/pedido/" + id_pedido + "?payment=cancelled")
                                .addAllLineItem(lista)
                                .build();

                Session session = Session.create(sessionCreateParams);
                ObjectMapper mapper = new ObjectMapper();
                String json = mapper.writeValueAsString(session.getUrl());
                return ResponseEntity.ok().body(json);
        }

        @Override
        public ResponseEntity<String> webhook(String signHeader, String payload) {
                Stripe.apiKey = secretkey;
                Event event = null;

                try {
                        // Construir el evento usando el webhook secret
                        event = Webhook.constructEvent(payload, signHeader, webhookSecret);

                        log.info("Webhook recibido: " + event.getType());

                        // Manejar el evento según su tipo
                        switch (event.getType()) {
                                case "checkout.session.completed":
                                        Session session = (Session) event.getDataObjectDeserializer()
                                                        .getObject().get();
                                        // Obtener el ID del pedido desde los metadatos
                                        String pedidoIdStr = session.getMetadata()
                                                        .get("pedido_id");
                                        log.info("Pedido ID: " + pedidoIdStr);
                                        if (pedidoIdStr == null) {
                                                // Intentar obtener desde la sesión directamente
                                                PaymentIntent paymentIntent = PaymentIntent
                                                                .retrieve(session.getPaymentIntent());
                                                pedidoIdStr = paymentIntent.getMetadata().get("pedido_id");
                                        }

                                        if (pedidoIdStr != null) {
                                                int pedidoId = Integer.parseInt(pedidoIdStr);
                                                log.info("Procesando pago exitoso para pedido: " + pedidoId);

                                                // Procesar el pago exitoso (solo cambia estado a PAGADO)
                                                // NO procesa el stock, solo lo mantiene reservado
                                                ResponseEntity<String> resultado = pedidoService
                                                                .procesarPagoExitoso(pedidoId);

                                                if (resultado.getStatusCode().value() == 200) {

                                                        log.info("Pago procesado exitosamente para pedido: " + pedidoId
                                                                        +
                                                                        ". Stock permanece reservado hasta la entrega.");

                                                } else {
                                                        log.error("Error al procesar pago para pedido: " + pedidoId +
                                                                        " - " + resultado.getBody());
                                                }
                                        } else {
                                                log.error("No se pudo obtener el ID del pedido desde el webhook");
                                        }
                                        break;

                                case "payment_intent.payment_failed":
                                        PaymentIntent failedIntent = (PaymentIntent) event.getDataObjectDeserializer()
                                                        .getObject()
                                                        .orElseThrow(() -> new RuntimeException(
                                                                        "Error al deserializar PaymentIntent"));

                                        String failedPedidoIdStr = failedIntent.getMetadata().get("pedido_id");

                                        if (failedPedidoIdStr != null) {
                                                int pedidoId = Integer.parseInt(failedPedidoIdStr);
                                                log.warn("Pago fallido para pedido: " + pedidoId);

                                                // Aquí podrías implementar lógica adicional para manejar pagos fallidos
                                                // Por ejemplo:
                                                // - Enviar un email al cliente informando del fallo
                                                // - Después de X intentos fallidos, cancelar el pedido y liberar stock
                                                // - Registrar el intento fallido en una tabla de auditoría
                                        }
                                        break;

                                case "payment_intent.succeeded":
                                        // Evento alternativo que también indica pago exitoso
                                        PaymentIntent succeededIntent = (PaymentIntent) event
                                                        .getDataObjectDeserializer()
                                                        .getObject().get();

                                        String succeededPedidoIdStr = succeededIntent.getMetadata().get("pedido_id");

                                        if (succeededPedidoIdStr != null) {
                                                int pedidoId = Integer.parseInt(succeededPedidoIdStr);
                                                log.info("Payment intent succeeded para pedido: " + pedidoId);
                                                // Este evento es redundante si ya procesamos checkout.session.completed
                                                // pero es bueno tenerlo como respaldo
                                        }
                                        break;

                                case "checkout.session.expired":
                                        // La sesión de pago expiró sin completarse
                                        PaymentIntent expiredSession = (PaymentIntent) event.getDataObjectDeserializer()
                                                        .getObject().get();
                                        log.info("Sesión de pago expirada: " + expiredSession.getId());
                                        // Podrías implementar lógica para liberar el stock reservado
                                        // después de cierto tiempo si el pedido sigue pendiente
                                        break;

                                default:
                                        log.info("Evento no manejado: " + event.getType());
                        }

                        return ResponseEntity.ok().body("Webhook procesado exitosamente");

                } catch (SignatureVerificationException e) {
                        log.error("Error en la verificación de firma del webhook", e);
                        return ResponseEntity.badRequest()
                                        .body("Error de verificación de firma: " + e.getMessage());
                } catch (Exception e) {
                        log.error("Error al procesar el webhook", e);
                        return ResponseEntity.badRequest()
                                        .body("Error al procesar el webhook: " + e.getMessage());
                }
        }
}