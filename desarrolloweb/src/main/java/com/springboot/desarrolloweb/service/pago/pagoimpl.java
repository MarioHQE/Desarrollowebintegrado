package com.springboot.desarrolloweb.service.pago;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.springboot.desarrolloweb.dao.pedidorepository;
import com.springboot.desarrolloweb.entity.pedido;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import com.stripe.param.checkout.SessionCreateParams.LineItem;
import com.stripe.param.checkout.SessionCreateParams.Mode;

@Service
public class pagoimpl implements pagoservice {
        @Value("${stripe.secretkey}")
        String secretkey;
        @Autowired
        pedidorepository pedidodao;

        @Override
        public ResponseEntity<String> sesiondepago(int id_pedido) throws StripeException {
                pedido pedido = pedidodao.findById(id_pedido)
                                .orElseThrow(() -> new RuntimeException(
                                                "No se encontró el pedido con ID: " + id_pedido));
                Stripe.apiKey = secretkey;
                List<LineItem> lista = new ArrayList<>();
                pedido.getPedidoProducto().forEach((pp) -> {
                        LineItem lineItem = LineItem.builder()
                                        .setPriceData(LineItem.PriceData.builder()
                                                        .setUnitAmount((long) pp.getProductoSucursal().getProducto()
                                                                        .getPrecio() * 100)
                                                        .setCurrency("PEN")
                                                        .setProductData(LineItem.PriceData.ProductData.builder()
                                                                        .setName(pp.getProductoSucursal().getProducto()
                                                                                        .getNombre())
                                                                        .build())
                                                        .build())
                                        .setQuantity(Long.valueOf(pp.getCantidad()))
                                        .build();
                        lista.add(lineItem);
                });
                SessionCreateParams sessionCreateParams = SessionCreateParams.builder().setMode(Mode.PAYMENT)
                                .setCustomerEmail(pedido.getUsuario() != null ? pedido.getUsuario().getEmail()
                                                : pedido.getEmail())
                                .putMetadata("pedido_id", String.valueOf(id_pedido))
                                .setPaymentIntentData(SessionCreateParams.PaymentIntentData.builder()
                                                .putMetadata("pedido_id", String.valueOf(id_pedido)).build())
                                .setSuccessUrl("http://localhost:4200/pedido/" + id_pedido)
                                .setCancelUrl("http://localhost:4200")
                                .addAllLineItem(null).build();
                Session session = Session.create(sessionCreateParams);

                return ResponseEntity.ok().body(session.getUrl());
        }

}
