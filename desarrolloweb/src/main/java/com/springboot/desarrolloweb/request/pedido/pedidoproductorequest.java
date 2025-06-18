package com.springboot.desarrolloweb.request.pedido;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "Producto individual dentro de un pedido")
public class pedidoproductorequest {

    @NotNull
    @Schema(description = "ID del producto en sucursal específica", example = "1", required = true)
    private int idProductoSucursal;

    @NotNull
    @Schema(description = "Cantidad del producto solicitada", example = "2", required = true)
    private int cantidad;
}
