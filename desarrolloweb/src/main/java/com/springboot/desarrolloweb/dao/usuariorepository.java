package com.springboot.desarrolloweb.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.springboot.desarrolloweb.entity.usuario;

@Repository
public interface usuariorepository extends JpaRepository<usuario, Integer> {
    public usuario findByEmail(@Param("email") String email);
}
