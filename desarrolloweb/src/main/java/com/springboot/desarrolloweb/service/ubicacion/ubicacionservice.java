package com.springboot.desarrolloweb.service.ubicacion;

import java.util.Set;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.springboot.desarrolloweb.entity.ubicacion;
import com.springboot.desarrolloweb.request.ubicacion.ubicacionpersonalrequest;

@Service
public interface ubicacionservice {

    public ResponseEntity<?> getUbicacionesbyusuario(String email);

    public ResponseEntity<?> agregarubicacionpersonal(ubicacionpersonalrequest ubicacionpersonalrequest);

}
