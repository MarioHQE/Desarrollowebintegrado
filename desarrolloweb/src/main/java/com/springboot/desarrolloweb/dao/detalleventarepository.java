package com.springboot.desarrolloweb.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.springboot.desarrolloweb.entity.detalleventa;

@Repository
public interface detalleventarepository extends JpaRepository<detalleventa, Integer> {

}
