package com.springboot.desarrolloweb.entity;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;

@Entity
@Data
@AllArgsConstructor
@DynamicInsert
@DynamicUpdate
public class detalleventa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int iddetalleventa;

    @Column(name = "cantidad")
    private double cantidad;

    @Column(name = "precio")
    private double precio;
    @Column(name = "descripción")
    private String descripcion;
    @Column(name = "subtotal")
    private double subtotal;

    @Column(name = "estado")
    private String estado;
    @ManyToOne
    @JoinColumn(name = "idproducto", referencedColumnName = "idproducto", foreignKey = @ForeignKey(name = "FK_producto_venta"))
    private producto idproducto;

    @ManyToOne
    @JoinColumn(name = "idventa", referencedColumnName = "idventa", foreignKey = @ForeignKey(name = "FK_venta_detalle"))
    private venta idventa;

    public double calcularSubtotal() {
        return this.cantidad * this.precio;
    }

    public void setCantidad(double cantidad) {
        this.cantidad = cantidad;
        this.subtotal = calcularSubtotal();
    }

    public void setPrecio(double precio) {
        this.precio = precio;
        this.subtotal = calcularSubtotal();
    }
}
