package com.springboot.desarrolloweb.service.pedido;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.springboot.desarrolloweb.entity.pedido;
import com.springboot.desarrolloweb.request.pedido.pedidorequest;
import com.springboot.desarrolloweb.request.pedido.pedidoupdaterequest;

@Service
public interface pedidoservice {
    List<pedido> getPedidos();

    pedido getPedido(int idPedido);

    ResponseEntity<String> createPedido(pedidorequest pedido);

    ResponseEntity<String> updatePedido(int idPedido, pedidoupdaterequest pedido);

    ResponseEntity<String> deletePedido(int idPedido);

    pedido pedidobyusuario(String email);

}
