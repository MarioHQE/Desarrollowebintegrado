package com.springboot.desarrolloweb.service.pago;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.stripe.exception.StripeException;

@Service
public interface pagoservice {
    public ResponseEntity<String> sesiondepago(int idpedido) throws StripeException, JsonProcessingException;

    public ResponseEntity<String> webhook(String payload, String sigHeader)
            throws JsonProcessingException, StripeException;

}
