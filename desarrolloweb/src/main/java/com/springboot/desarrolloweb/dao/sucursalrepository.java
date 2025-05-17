package com.springboot.desarrolloweb.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.springboot.desarrolloweb.entity.sucursal;

public interface sucursalrepository extends JpaRepository<sucursal, Integer> {
    boolean existsByCodigoPropio(String codigoPropio);
}
