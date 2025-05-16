package com.springboot.desarrolloweb.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;

@Entity
@Data
@AllArgsConstructor
@DynamicInsert
@DynamicUpdate
public class pedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idpedido")
    private int idpedido;
    @Column(name = "nombre")
    private String nombre;
    @Column(name = "apellido")
    private String apellido;
    @Column(name = "direccion")
    private String direccion;
    @Column(name = "telefono")
    private String telefono;
    @Column(name = "email")
    private String email;
    @Column(name = "fechaderecojo", nullable = true)
    private LocalDateTime fechaderecojo;
    @Column(name = "fecha")
    private LocalDateTime fecha;
    @Column(name = "fechapago", nullable = true)
    private LocalDateTime fechapago;
    @Column(name = "estado")
    private String estado;

}
