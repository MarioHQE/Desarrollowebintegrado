package com.springboot.desarrolloweb.request.producto;

import io.micrometer.common.lang.NonNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class productosucursalrequest {
    @NonNull
    @NotBlank
    @PositiveOrZero
    private int stock;
    @NonNull
    @Positive(message = "El id del producto debe ser mayor a 0")
    private int producto;
    @NonNull
    @NotBlank
    @Positive
    private int sucursal;

}
