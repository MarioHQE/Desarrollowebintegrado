package com.springboot.desarrolloweb.service.pedido;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.springboot.desarrolloweb.entity.pedido;
import com.springboot.desarrolloweb.request.pedido.pedidorequest;
import com.springboot.desarrolloweb.request.pedido.pedidoupdaterequest;

@Service
public interface pedidoservice {
    List<pedido> getPedidos();

    pedido getPedido(int idPedido);

    ResponseEntity<String> createPedido(pedidorequest pedido) throws JsonProcessingException;

    ResponseEntity<String> updatePedido(int idPedido, pedidoupdaterequest pedido);

    ResponseEntity<String> deletePedido(int idPedido);

    List<pedido> pedidobyusuario(String email);

}
