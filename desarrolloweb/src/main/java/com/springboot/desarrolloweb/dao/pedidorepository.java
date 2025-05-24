package com.springboot.desarrolloweb.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.springboot.desarrolloweb.entity.pedido;

@Repository
public interface pedidorepository extends JpaRepository<pedido, Integer> {

}
