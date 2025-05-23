package com.springboot.desarrolloweb.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.springboot.desarrolloweb.entity.pedido;
import com.springboot.desarrolloweb.request.pedido.pedidorequest;
import com.springboot.desarrolloweb.request.pedido.pedidoupdaterequest;
import com.springboot.desarrolloweb.service.pedido.pedidoimpl;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/pedido")
public class pedidocontroller {
    @Autowired
    private pedidoimpl pedidoimpl;

    @GetMapping("/all")
    public List<pedido> pedidos() {
        return pedidoimpl.getPedidos();
    }

    @GetMapping("/pedido")
    public pedido pedido(@RequestParam(name = "idpedido") int idpedido) {
        return pedidoimpl.getPedido(idpedido);
    }

    @PostMapping("/save")
    public ResponseEntity<String> guardarpedido(@RequestBody pedidorequest request) throws JsonProcessingException {
        return pedidoimpl.createPedido(request);
    }

    @PutMapping("/update/{idpedido}")
    public ResponseEntity<String> putMethodName(@PathVariable("idpedido") int idpedido,
            @RequestBody pedidoupdaterequest request) {

        return pedidoimpl.updatePedido(idpedido, request);
    }

    @DeleteMapping("/delete/{idpedido}")
    public ResponseEntity<String> deletepedido(@PathVariable("idpedido") int idpedido) {
        return pedidoimpl.deletePedido(idpedido);
    }

}
