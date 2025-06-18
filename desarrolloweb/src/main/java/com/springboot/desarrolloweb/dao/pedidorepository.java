package com.springboot.desarrolloweb.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.springboot.desarrolloweb.entity.pedido;

@Repository
public interface pedidorepository extends JpaRepository<pedido, Integer> {
    List<pedido> findbyusuario(@Param("email") String email);
}
