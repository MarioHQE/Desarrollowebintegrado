package com.springboot.desarrolloweb.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "DTO para información básica de producto")
public class productoDTO {

    @Schema(description = "Nombre del producto", example = "Queso Fresco")
    private String nombre;

    @Schema(description = "Descripción detallada del producto", example = "Queso fresco artesanal de leche de vaca")
    private String descripcion;

    @Schema(description = "Precio del producto en soles", example = "15.50")
    private double precio;
}