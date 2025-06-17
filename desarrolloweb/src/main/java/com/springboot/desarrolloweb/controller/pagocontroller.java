package com.springboot.desarrolloweb.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.springboot.desarrolloweb.service.pago.pagoimpl;
import com.stripe.exception.StripeException;

import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@Controller
@RequestMapping("/pago")
@Slf4j
public class pagocontroller {
    @Value("${stripe.secretkey}")
    String secretkey;
    @Autowired
    pagoimpl pagoservice;

    @GetMapping("/url/{idpedido}")
    public ResponseEntity<String> conseguirurl(@PathVariable(name = "idpedido") int idpedido)
            throws StripeException, JsonProcessingException {

        return pagoservice.sesiondepago(idpedido);
    }

    @PostMapping("/webhook")
    public ResponseEntity<String> StripeWebhook(@RequestHeader("Stripe-Signature") String SignHeader,
            @RequestBody String Payload)
            throws JsonProcessingException, StripeException {
        log.info("Webhook received with signature: {}", SignHeader);
        return pagoservice.webhook(SignHeader, Payload);
    }

}
