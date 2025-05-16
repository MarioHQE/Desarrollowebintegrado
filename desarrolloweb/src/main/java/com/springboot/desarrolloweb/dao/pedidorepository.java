package com.springboot.desarrolloweb.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.springboot.desarrolloweb.entity.pedido;

public interface pedidorepository extends JpaRepository<pedido, Integer> {

}
