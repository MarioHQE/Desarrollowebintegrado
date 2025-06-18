package com.springboot.desarrolloweb.request.pedido;

import java.time.LocalDateTime;
import java.util.List;

import io.micrometer.common.lang.NonNull;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Request para crear un nuevo pedido")
public class pedidorequest {

    @NotEmpty
    @NotBlank
    @Schema(description = "Nombre del cliente", example = "Juan", required = true)
    private String nombre;

    @NotEmpty
    @NotBlank
    @Schema(description = "Apellido del cliente", example = "Pérez", required = true)
    private String apellido;

    @NotEmpty
    @NotBlank
    @NotNull
    @Schema(description = "Dirección de entrega", example = "Av. Principal 123, San Isidro", required = true)
    private String direccion;

    @Digits(fraction = 0, integer = 9)
    @Schema(description = "Teléfono del cliente", example = "987654321", required = true)
    private String telefono;

    @Email
    @NotEmpty
    @NotBlank
    @Schema(description = "Email del cliente", example = "juan@example.com", required = true)
    private String email;

    @Schema(description = "Fecha y hora de recojo preferida", example = "2024-01-15T10:00:00")
    private LocalDateTime fechaderecojo;

    @NotNull
    @NotEmpty
    @Schema(description = "Fecha y hora del pedido", example = "2024-01-10T14:30:00", required = true)
    private LocalDateTime fechapedido;

    @Schema(description = "Fecha y hora del pago (se establece automáticamente)")
    private LocalDateTime fechapago;

    @NonNull
    @Schema(description = "Lista de productos del pedido", required = true)
    private List<pedidoproductorequest> productos;

    @Schema(description = "Estado del pedido", example = "PENDIENTE", allowableValues = { "PENDIENTE", "PAGADO",
            "PREPARANDO", "LISTO_PARA_RECOGER", "ENTREGADO", "CANCELADO" })
    private String estado;
}