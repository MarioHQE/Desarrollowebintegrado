package com.springboot.desarrolloweb.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.springboot.desarrolloweb.entity.producto;

@Repository
public interface productorepository extends JpaRepository<producto, Integer> {

}