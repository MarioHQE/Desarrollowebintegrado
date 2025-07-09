package com.springboot.desarrolloweb.response;

import com.springboot.desarrolloweb.entity.ubicacion;

import lombok.Data;

@Data
public class agregarubicacionresponse {
    String mensaje;
    ubicacion ubicacion;

    public agregarubicacionresponse(String mensaje, ubicacion ubicacion) {
        this.mensaje = mensaje;
        this.ubicacion = ubicacion;
    }
}
