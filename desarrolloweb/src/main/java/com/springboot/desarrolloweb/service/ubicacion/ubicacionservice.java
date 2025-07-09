package com.springboot.desarrolloweb.service.ubicacion;

import java.util.Set;

import org.springframework.stereotype.Service;

import com.springboot.desarrolloweb.entity.ubicacion;

@Service
public interface ubicacionservice {

    public Set<ubicacion> getUbicacionesbyusuario(String email);

}
