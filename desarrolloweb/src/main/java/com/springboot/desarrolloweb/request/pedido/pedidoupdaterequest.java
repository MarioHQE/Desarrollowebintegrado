package com.springboot.desarrolloweb.request.pedido;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class pedidoupdaterequest {
    private String nombre;
    private String apellido;
    private String direccion;
    private String telefono;
    private String email;
    private LocalDateTime fechaderecojo;

    private LocalDateTime fechapedido;
    private LocalDateTime fechapago;

    private String estado;
}
