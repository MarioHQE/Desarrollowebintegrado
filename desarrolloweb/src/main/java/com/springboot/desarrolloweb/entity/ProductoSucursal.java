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
import jakarta.persistence.NamedQuery;
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
@NamedQuery(name = "ProductoSucursal.findProductosBySucursal", query = "SELECT ps FROM ProductoSucursal ps WHERE ps.sucursal.idsucursal = :idSucursal")
@NamedQuery(name = "ProductoSucursal.findbyproductoysucursal", query = "SELECT ps FROM ProductoSucursal ps WHERE ps.producto.idproducto=:idProducto AND ps.sucursal.idsucursal=:idSucursal")
public class ProductoSucursal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idproductosucursal")
    private int idProductoSucursal;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "idproducto", referencedColumnName = "idproducto", foreignKey = @ForeignKey(name = "fk_producto"))
    private producto producto;
    @ManyToOne
    @JoinColumn(name = "idsucursal", referencedColumnName = "idsucursal", foreignKey = @ForeignKey(name = "fk_sucursal"))
    private sucursal sucursal;
    @JsonIgnore
    @OneToMany(mappedBy = "productoSucursal", fetch = FetchType.EAGER)
    private List<pedidoproducto> pedidoProducto;
    @Column(name = "stock")
    private int stock;
    @Column(name = "stock_reservado", columnDefinition = "int default 0")
    private int stockReservado;

}
