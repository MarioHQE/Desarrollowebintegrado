package com.springboot.desarrolloweb.request.sucursal;

import lombok.Data;

@Data
public class sucursalupdaterequest {

    private String nombre;

    private String direccion;

    private String ciudad;

    private double lon;

    private double lat;
}
