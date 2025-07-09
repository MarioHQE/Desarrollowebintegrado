package com.springboot.desarrolloweb.entity;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import jakarta.persistence.ForeignKey;
import lombok.NoArgsConstructor;

@Entity
@DynamicInsert
@DynamicUpdate
@Data
@AllArgsConstructor
@NoArgsConstructor
@NamedQuery(name = "pedido.findbyusuario", query = "SELECT p FROM pedido p WHERE p.usuario.email = :email")
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

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<pedidoproducto> pedidoProducto;
    @ManyToOne(optional = true)
    @JoinColumn(name = "idusuario", nullable = true, referencedColumnName = "idusuario", foreignKey = @ForeignKey(name = "fk_pedido_usuario"))
    private usuario usuario;
    @Column(name = "stock_procesado", columnDefinition = "boolean default false")
    private boolean stockProcesado;
}