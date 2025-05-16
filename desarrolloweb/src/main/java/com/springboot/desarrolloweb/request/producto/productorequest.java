package com.springboot.desarrolloweb.request.producto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class productorequest {
    @NotBlank
    private String nombre;

    @NotBlank
    private String descripcion;

    @PositiveOrZero
    private double precio;

    @NotBlank
    private String imagen;

    @NotNull
    private Boolean estado;

    @NotNull
    private Integer idcategoria;

}
