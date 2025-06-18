package com.springboot.desarrolloweb.request.producto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request para crear un nuevo producto")
public class productorequest {

    @NotNull
    @NotEmpty
    @Schema(description = "Nombre del producto", example = "Queso Fresco", required = true)
    private String nombre;

    @NotNull
    @Size(max = 300)
    @Schema(description = "Descripción del producto", example = "Queso fresco artesanal de leche de vaca", required = true)
    private String descripcion;

    @PositiveOrZero
    @Schema(description = "Precio del producto en soles", example = "15.50", required = true)
    private double precio;

    @NotNull
    @NotEmpty
    @Schema(description = "URL de la imagen del producto", example = "https://example.com/queso.jpg", required = true)
    private String imagen;

    @NotNull
    @Schema(description = "Estado del producto (activo/inactivo)", example = "true", required = true)
    private Boolean estado;

    @NotNull
    @Schema(description = "ID de la categoría del producto", example = "1", required = true)
    private Integer idcategoria;
}
