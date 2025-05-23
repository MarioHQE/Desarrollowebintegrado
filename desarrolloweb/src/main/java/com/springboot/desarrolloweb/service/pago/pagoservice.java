package com.springboot.desarrolloweb.service.pago;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.stripe.exception.StripeException;

@Service
public interface pagoservice {
    public ResponseEntity<String> sesiondepago(int idpedido) throws StripeException;

}
