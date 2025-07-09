package com.springboot.desarrolloweb.request.ubicacion;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class ubicacionpersonalrequest {
    @NotEmpty
    @Email(message = "El email debe ser válido")
    String email;
    @NotEmpty
    String ubicacion;
    @NotEmpty
    double longitud;
    @NotEmpty
    double latitud;
}
