package com.springboot.desarrolloweb.entity;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.fasterxml.jackson.annotation.JsonIgnore;

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
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@DynamicInsert
@DynamicUpdate
public class pedidoproducto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int pedidoproductoid;
    @ManyToOne
    @JoinColumn(name = "idproductosucursal", referencedColumnName = "idproductosucursal", foreignKey = @ForeignKey(name = "FK_producto_sucursal"))
    private ProductoSucursal productoSucursal;
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "idpedido", referencedColumnName = "idpedido", foreignKey = @ForeignKey(name = "FK_pedido_producto"))
    private pedido pedido;
    @Column(name = "cantidad")
    private int cantidad;
    @Column(name = "subtotal")
    private double subtotal;

}
