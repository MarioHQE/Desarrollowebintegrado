package com.springboot.desarrolloweb.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.springboot.desarrolloweb.entity.ubicacion;

@Repository
public interface ubicacionrepository extends JpaRepository<ubicacion, Integer> {

}
