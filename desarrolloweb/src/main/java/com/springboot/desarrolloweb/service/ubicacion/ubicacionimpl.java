package com.springboot.desarrolloweb.service.ubicacion;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.springboot.desarrolloweb.dao.ubicacion_usuariorepository;
import com.springboot.desarrolloweb.dao.ubicacionrepository;
import com.springboot.desarrolloweb.dao.usuariorepository;
import com.springboot.desarrolloweb.entity.ubicacion;
import com.springboot.desarrolloweb.entity.ubicacion_usuario;
import com.springboot.desarrolloweb.entity.usuario;
import com.springboot.desarrolloweb.request.ubicacion.ubicacionpersonalrequest;
import com.springboot.desarrolloweb.response.agregarubicacionresponse;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ubicacionimpl implements ubicacionservice {
    @Autowired
    ubicacion_usuariorepository ubicacion_usuariorepository;

    @Autowired
    ubicacionrepository ubicacionrepository;

    @Autowired
    usuariorepository usuariorepository;

    @Override
    @Transactional(readOnly = true)
    public Set<ubicacion> getUbicacionesbyusuario(String email) {
        // PROBLEMA: No puedes usar Set directamente porque causa el ciclo infinito
        List<ubicacion_usuario> ubicaciones = ubicacion_usuariorepository.findbyusuario(email);

        // SOLUCIÓN: Convertir a List primero (evita el cálculo de hashCode)
        Set<ubicacion> ubicacionesencontradas = ubicaciones.stream()
                .map(ubicacion_usuario::getUbicacion)
                .collect(Collectors.toSet());

        return ubicacionesencontradas;
    }

    @Transactional
    public ResponseEntity<?> agregarubicacionpersonal(ubicacionpersonalrequest ubicacionpersonalrequest) {
        log.info("email: " + ubicacionpersonalrequest.getEmail());

        usuario usuario = usuariorepository.findByEmail(ubicacionpersonalrequest.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!usuario.isEnabled()) {
            return ResponseEntity.badRequest().body("El usuario no se ha verificado");
        }

        ubicacion ubicacion = new ubicacion();
        ubicacion.setLatitud(ubicacionpersonalrequest.getLatitud());
        ubicacion.setLongitud(ubicacionpersonalrequest.getLongitud());
        ubicacion.setUbicacion(ubicacionpersonalrequest.getUbicacion());

        // Guardar primero la ubicación
        ubicacion ubicacionGuardada = ubicacionrepository.save(ubicacion);

        ubicacion_usuario ubicacion_usuario = new ubicacion_usuario();
        ubicacion_usuario.setUbicacion(ubicacionGuardada);
        ubicacion_usuario.setUsuario(usuario);
        ubicacion_usuariorepository.save(ubicacion_usuario);

        agregarubicacionresponse agregarubicacionresponse = new agregarubicacionresponse("ubicacion agregada",
                ubicacionGuardada);

        return ResponseEntity.ok().body(agregarubicacionresponse);
    }
}