package com.springboot.desarrolloweb.entity;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@DynamicInsert
@DynamicUpdate
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "sucursal")
public class sucursal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idsucursal;
    @Column(name = "nombre")
    private String nombre;
    @Column(name = "direccion")
    private String direccion;
    @Column(name = "longitud")
    private double lon;
    @Column(name = "latitud")
    private double lat;
    @Column(name = "ciudad")
    private String ciudad;
    @Column(name = "codigo_propio", unique = true)
    private String codigoPropio;

}