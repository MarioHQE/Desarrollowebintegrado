package com.springboot.desarrolloweb.service.ubicacion;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.springboot.desarrolloweb.dao.ubicacion_usuariorepository;
import com.springboot.desarrolloweb.dao.ubicacionrepository;
import com.springboot.desarrolloweb.dao.usuariorepository;
import com.springboot.desarrolloweb.entity.ubicacion;
import com.springboot.desarrolloweb.entity.ubicacion_usuario;
import com.springboot.desarrolloweb.entity.usuario;
import com.springboot.desarrolloweb.request.ubicacion.ubicacionpersonalrequest;
import com.springboot.desarrolloweb.response.agregarubicacionresponse;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.validation.Valid;

@Service
public class ubicacionimpl implements ubicacionservice {
    @Autowired
    ubicacion_usuariorepository ubicacion_usuariorepository;

    @Autowired
    ubicacionrepository ubicacionrepository;
    @Autowired
    usuariorepository usuariorepository;

    @Override
    public Set<ubicacion> getUbicacionesbyusuario(String email) {
        Set<ubicacion_usuario> ubicaciones = ubicacion_usuariorepository.findbyusuario(email);

        Set<ubicacion> ubicacionesencontradas = ubicaciones.stream()
                .map(ubicacion_usuario::getUbicacion).collect(Collectors.toSet());
        return ubicacionesencontradas;
    }

    public ResponseEntity<?> agregarubicacionpersonal(
            @Valid @RequestBody ubicacionpersonalrequest ubicacionpersonalrequest) {
        {

            usuario usuario = usuariorepository.findByEmail(ubicacionpersonalrequest.getEmail())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            ubicacion ubicacion = new ubicacion();
            if (usuario.isEnabled() == false) {
                return ResponseEntity.badRequest().body("El usuario no se ha verificado");
            }

            ubicacion_usuario ubicacion_usuario = new ubicacion_usuario();
            ubicacion.setLatitud(ubicacionpersonalrequest.getLatitud());
            ubicacion.setLongitud(ubicacionpersonalrequest.getLongitud());
            ubicacion_usuario.setUbicacion(ubicacion);
            ubicacion_usuario.setUsuario(usuario);
            ubicacionrepository.save(ubicacion);
            ubicacion_usuariorepository.save(ubicacion_usuario);
            agregarubicacionresponse agregarubicacionresponse = new agregarubicacionresponse("ubicacion agregada",
                    ubicacion);

            return ResponseEntity.ok().body(agregarubicacionresponse);
        }
    }

}
