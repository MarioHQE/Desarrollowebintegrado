package com.springboot.desarrolloweb.entity;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Entity
@Data
@DynamicInsert
@DynamicUpdate
@Getter
@Setter
@NamedQuery(name = "rol.findbynombre", query = "SELECT r FROM rol r WHERE r.nombre  = :nombre")
@Table(name = "rol")
public class rol {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "idrol")
    private int idrol;
    @Column(name = "nombre")
    private String nombre;

}
