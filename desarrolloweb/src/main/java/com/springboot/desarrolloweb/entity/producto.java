package com.springboot.desarrolloweb.entity;

import java.util.List;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@DynamicInsert
@DynamicUpdate

public class producto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idproducto")
    private int idproducto;
    @Column(name = "nombre")
    private String nombre;
    @Column(name = "descripcion")
    private String descripcion;
    @Column(name = "precio")
    private double precio;
    @Column(name = "imagen")
    private String imagen;

    @ManyToOne
    @JoinColumn(name = "idcategoria", referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_categoria_producto"))
    private categoria categoria;
    @Column(name = "estado")
    private boolean estado;

    @JsonIgnore
    @OneToMany(mappedBy = "producto", fetch = FetchType.EAGER)
    private List<ProductoSucursal> productoSucursal;
}
