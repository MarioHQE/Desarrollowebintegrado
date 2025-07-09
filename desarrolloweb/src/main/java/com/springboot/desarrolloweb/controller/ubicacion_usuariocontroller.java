package com.springboot.desarrolloweb.controller;

import org.springframework.web.bind.annotation.RestController;

import com.springboot.desarrolloweb.entity.ubicacion;
import com.springboot.desarrolloweb.service.ubicacion.ubicacionimpl;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
public class ubicacion_usuariocontroller {
    @Autowired
    private ubicacionimpl ubicacionimpl;

    @PostMapping("/ubicacion-usuario/")
    public Set<ubicacion> ubicacionbyusuario(@RequestParam String email) {

        // TODO: process POST request

        return ubicacionimpl.getUbicacionesbyusuario(email);
    }

}
