package com.springboot.desarrolloweb.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.springboot.desarrolloweb.entity.sucursal;
import java.util.List;

@Repository
public interface sucursalrepository extends JpaRepository<sucursal, Integer> {
    boolean existsByCodigoPropio(String codigoPropio);

    List<sucursal> findByCiudad(String ciudad);

}
