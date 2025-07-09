package com.springboot.desarrolloweb.controller;

import org.springframework.web.bind.annotation.RestController;

import com.springboot.desarrolloweb.entity.ubicacion;
import com.springboot.desarrolloweb.request.ubicacion.ubicacionpersonalrequest;
import com.springboot.desarrolloweb.response.agregarubicacionresponse;
import com.springboot.desarrolloweb.service.ubicacion.ubicacionimpl;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/ubicacion-usuario")
public class ubicacion_usuariocontroller {
    @Autowired
    private ubicacionimpl ubicacionimpl;

    @GetMapping("/{email}")
    public ResponseEntity<?> ubicacion(@PathVariable String email) {
        return ubicacionimpl.getUbicacionesbyusuario(email);
    }

    @PostMapping("/agregar")
    public ResponseEntity<?> agregarubicacionbyusuario(
            @RequestBody ubicacionpersonalrequest ubicacionpersonalrequest) {

        return ubicacionimpl.agregarubicacionpersonal(ubicacionpersonalrequest);
    }

}
