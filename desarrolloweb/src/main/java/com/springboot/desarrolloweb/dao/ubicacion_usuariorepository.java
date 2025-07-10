package com.springboot.desarrolloweb.dao;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.springboot.desarrolloweb.entity.ubicacion_usuario;

@Repository
public interface ubicacion_usuariorepository extends JpaRepository<ubicacion_usuario, Integer> {
    public List<ubicacion_usuario> findbyusuario(@Param("email") String email);

}
