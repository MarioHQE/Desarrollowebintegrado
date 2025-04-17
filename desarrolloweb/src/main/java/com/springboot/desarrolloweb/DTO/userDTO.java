package com.springboot.desarrolloweb.DTO;

import com.springboot.desarrolloweb.entity.usuario;

import lombok.Data;

@Data
public class userDTO {
    private String nombre;
    private String apellido;
    private String email;
    private String telefono;

    public userDTO(usuario user) {
        this.apellido = user.getApellido();
        this.nombre = user.getNombre();
        this.telefono = user.getTelefono();
        this.email = user.getEmail();
    }

}
