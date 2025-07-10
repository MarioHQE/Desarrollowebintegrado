package com.springboot.desarrolloweb.entity;

import java.util.HashSet;
import java.util.Set;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@DynamicInsert
@DynamicUpdate
public class ubicacion {
    @Column(name = "idubicacion")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private int idubicacion;
    @Column(name = "ubicacion")
    private String ubicacion;
    @Column(name = "latitud")
    private double latitud;
    @Column(name = "longitud")
    private double longitud;
    @JsonIgnore
    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "ubicacion", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<ubicacion_usuario> ubicacion_usuario = new HashSet<>();

}
