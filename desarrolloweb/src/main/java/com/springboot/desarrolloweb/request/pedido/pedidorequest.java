package com.springboot.desarrolloweb.request.pedido;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class pedidorequest {

    @NotEmpty
    @NotBlank
    private String nombre;
    @NotEmpty
    @NotBlank
    private String apellido;
    @NotEmpty
    @NotBlank
    @NotNull
    private String direccion;
    @Digits(fraction = 0, integer = 9)
    private String telefono;
    @Email
    @NotEmpty
    @NotBlank
    private String email;

    private LocalDateTime fechaderecojo;
    @NotNull
    @NotEmpty
    private LocalDateTime fechapedido;
    private LocalDateTime fechapago;

    private String estado;

}
