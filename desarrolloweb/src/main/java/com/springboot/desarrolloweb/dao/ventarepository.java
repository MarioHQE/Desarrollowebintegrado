package com.springboot.desarrolloweb.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.springboot.desarrolloweb.entity.venta;

@Repository
public interface ventarepository extends JpaRepository<venta, Integer> {

}
