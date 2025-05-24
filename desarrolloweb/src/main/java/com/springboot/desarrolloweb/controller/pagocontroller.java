package com.springboot.desarrolloweb.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.springboot.desarrolloweb.service.pago.pagoservice;
import com.stripe.exception.StripeException;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/pago")
public class pagocontroller {
    @Autowired
    pagoservice pagoservice;

    @GetMapping("/url")
    public ResponseEntity<String> conseguirurl(@RequestParam int idpedido) throws StripeException {
        return pagoservice.sesiondepago(idpedido);
    }

}
