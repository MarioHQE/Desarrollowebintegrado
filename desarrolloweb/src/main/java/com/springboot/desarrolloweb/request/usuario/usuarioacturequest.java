package com.springboot.desarrolloweb.request.usuario;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class usuarioacturequest {

    private String nombre;

    private String apellido;

    @Email(message = "El email debe ser válido")
    private String email;

    @NotBlank(message = "El teléfono es obligatorio")
    @Pattern(regexp = "^9\\d{8}$", message = "El teléfono debe empezar con 9 y tener 9 dígitos")
    private String telefono;

    private String ciudad;

    @Pattern(regexp = "^\\d{8}$", message = "El DNI debe tener exactamente 8 dígitos")
    private String dni;
}
