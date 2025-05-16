package com.springboot.desarrolloweb.service.pedido;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import com.springboot.desarrolloweb.dao.pedidorepository;
import com.springboot.desarrolloweb.entity.pedido;
import com.springboot.desarrolloweb.request.pedido.pedidorequest;
import com.springboot.desarrolloweb.request.pedido.pedidoupdaterequest;

public class pedidoimpl implements pedidoservice {
    @Autowired
    pedidorepository pedidorepository;

    @Override
    public List<pedido> getPedidos() {
        return pedidorepository.findAll();
    }

    @Override
    public pedido getPedido(int idPedido) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getPedido'");
    }

    @Override
    public ResponseEntity<pedido> createPedido(pedidorequest pedido) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'createPedido'");
    }

    @Override
    public ResponseEntity<pedido> updatePedido(int idPedido, pedidoupdaterequest pedido) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updatePedido'");
    }

    @Override
    public ResponseEntity<String> deletePedido(int idPedido) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deletePedido'");
    }

    @Override
    public pedido pedidobyusuario(String email) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'pedidobyusuario'");
    }

}
