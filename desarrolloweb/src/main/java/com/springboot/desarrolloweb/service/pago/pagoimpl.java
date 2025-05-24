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
import com.springboot.desarrolloweb.entity.pedido;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import com.stripe.param.checkout.SessionCreateParams.LineItem;
import com.stripe.param.checkout.SessionCreateParams.Mode;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class pagoimpl implements pagoservice {
        @Value("${stripe.secretkey}")
        String secretkey;
        @Autowired
        pedidorepository pedidodao;

        @Override
        public ResponseEntity<String> sesiondepago(int id_pedido) throws StripeException, JsonProcessingException {
                pedido pedido = pedidodao.findById(id_pedido)
                                .orElseThrow(() -> new RuntimeException(
                                                "No se encontró el pedido con ID: " + id_pedido));
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
                                                        .setUnitAmount((long) pp.getProductoSucursal().getProducto()
                                                                        .getPrecio() * 100)
                                                        .setCurrency("PEN")

                                                        .build())
                                        .setQuantity(Long.valueOf(pp.getCantidad()))

                                        .build();
                        lista.add(lineItem);
                });

                SessionCreateParams sessionCreateParams = SessionCreateParams.builder().setMode(Mode.PAYMENT)
                                .setCustomerEmail(pedido.getUsuario() != null ? pedido.getUsuario().getEmail()
                                                : pedido.getEmail())
                                .setPaymentIntentData(SessionCreateParams.PaymentIntentData.builder()
                                                .putMetadata("pedido_id", String.valueOf(id_pedido)).build())
                                .setSuccessUrl("http://localhost:5173/pedido/" + id_pedido)
                                .setCancelUrl("http://localhost:5173")
                                .addAllLineItem(lista).build();
                Session session = Session.create(sessionCreateParams);
                ObjectMapper mapper = new ObjectMapper();
                String json = mapper.writeValueAsString(session.getUrl());
                return ResponseEntity.ok().body(json);
        }

}
