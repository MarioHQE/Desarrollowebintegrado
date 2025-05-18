package com.springboot.desarrolloweb.request.categoria;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class categoriaupdaterequest {
    private String name;
}
