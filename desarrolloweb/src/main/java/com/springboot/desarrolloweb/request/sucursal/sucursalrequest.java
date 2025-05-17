package com.springboot.desarrolloweb.request.sucursal;

import io.micrometer.common.lang.NonNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class sucursalrequest {
    @NotBlank
    @NotEmpty
    private String nombre;
    @NotBlank
    @NotEmpty
    private String direccion;
    @NotBlank
    @NotEmpty
    private String ciudad;

    private String codigo_propio;
    @NotBlank
    @NotEmpty
    private double lon;
    @NotBlank
    @NotEmpty
    private double lat;

    public String generarcodigo() {
        return this.getNombre().substring(0, 3) + "_" + this.getCiudad().substring(0, 3);
    }
}
