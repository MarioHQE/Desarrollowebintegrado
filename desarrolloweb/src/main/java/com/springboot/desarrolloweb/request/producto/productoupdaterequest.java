package com.springboot.desarrolloweb.request.producto;

import lombok.Data;

@Data
public class productoupdaterequest {
    private String nombre;
    private String descripcion;
    private Double precio;
    private String imagen;
    private Boolean estado;
    private Integer idcategoria;

}
