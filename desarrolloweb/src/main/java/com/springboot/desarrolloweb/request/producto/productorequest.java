package com.springboot.desarrolloweb.request.producto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class productorequest {
    @NotNull
    @NotEmpty
    private String nombre;

    @NotNull
    @Size(max = 300)
    private String descripcion;

    @PositiveOrZero
    private double precio;

    @NotNull
    private String imagen;

    @NotNull
    private Boolean estado;

    @NotNull
    private Integer idcategoria;

}
