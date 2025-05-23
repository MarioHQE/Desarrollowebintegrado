package com.springboot.desarrolloweb.request.categoria;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class categoriarequest {
    @NotEmpty
    @NotNull
    private String name;

}