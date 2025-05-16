package com.springboot.desarrolloweb.request.producto;

import io.micrometer.common.lang.NonNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class productosucursalupdaterequest {
    @NonNull
    @NotBlank
    private Integer idproductosucursal;
    @PositiveOrZero
    private Integer stock;

    private Integer producto;

    private Integer sucursal;
}
