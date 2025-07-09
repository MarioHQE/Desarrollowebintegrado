package com.springboot.desarrolloweb.entity;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
    @Column(name = "activo", columnDefinition = "boolean default true")
    private boolean activo;
    @Column(name = "eliminado", columnDefinition = "boolean default false")
    private boolean eliminado;
    @Column(name = "fecha_eliminacion")
    private LocalDateTime fechaEliminacion;
    @Column(name = "motivo_eliminacion")
    private String motivoEliminacion;
    @Enumerated(EnumType.STRING)
    @Column(name = "estado")
    private estado estado;
    @Column(name = "stock", columnDefinition = "int default 0")
    private int stock;
    @Column(name = "stock_reservado", columnDefinition = "int default 0")
    private int stockReservado;

    public static enum estado {
        ACTIVO, INACTIVO, ELIMINADO;

    }
}
