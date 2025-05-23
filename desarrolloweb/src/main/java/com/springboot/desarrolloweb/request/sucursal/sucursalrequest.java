package com.springboot.desarrolloweb.request.sucursal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class sucursalrequest {
    @NotBlank
    @NotEmpty
    private String nombre;
    @NotNull
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
