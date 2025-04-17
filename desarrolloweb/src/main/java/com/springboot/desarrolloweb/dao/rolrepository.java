package com.springboot.desarrolloweb.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.springboot.desarrolloweb.entity.rol;

@Repository
public interface rolrepository extends JpaRepository<rol, Integer> {
    public rol findbynombre(@Param("nombre") String nombre);
}