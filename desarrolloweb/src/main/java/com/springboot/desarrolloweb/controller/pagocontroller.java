package com.springboot.desarrolloweb.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.springboot.desarrolloweb.service.pago.pagoimpl;
import com.stripe.exception.StripeException;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/pago")
public class pagocontroller {
    @Autowired
    pagoimpl pagoservice;

    @GetMapping("/url/{idpedido}")
    public ResponseEntity<String> conseguirurl(@PathVariable(name = "idpedido") int idpedido)
            throws StripeException, JsonProcessingException {
        return pagoservice.sesiondepago(idpedido);
    }

}
