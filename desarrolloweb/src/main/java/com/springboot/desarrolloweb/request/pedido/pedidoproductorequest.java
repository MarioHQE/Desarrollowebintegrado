package com.springboot.desarrolloweb.request.pedido;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class pedidoproductorequest {
    @NotNull
    private int idProductoSucursal;

    @NotNull
    private int cantidad;

}
