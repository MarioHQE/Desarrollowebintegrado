package com.springboot.desarrolloweb.request.ubicacion;

import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class ubicacionpersonalrequest {
    @Email(message = "El email debe ser válido")
    String email;

    String ubicacion;

    double longitud;

    double latitud;

}
