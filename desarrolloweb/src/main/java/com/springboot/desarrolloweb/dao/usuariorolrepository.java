package com.springboot.desarrolloweb.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.springboot.desarrolloweb.entity.usuariorol;

@Repository
public interface usuariorolrepository extends JpaRepository<usuariorol, Integer> {

}
