package com.springboot.desarrolloweb.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "DTO para información básica del usuario")
public class userDTO {

    @Schema(description = "Nombre del usuario", example = "Juan")
    private String nombre;

    @Schema(description = "Apellido del usuario", example = "Pérez")
    private String apellido;

    @Schema(description = "Email del usuario", example = "juan@example.com")
    private String email;

    @Schema(description = "Teléfono del usuario", example = "987654321")
    private String telefono;

    @Schema(description = "Código de verificación (solo para uso interno)", example = "123456")
    private String verificationCode;
}
