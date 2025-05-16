package com.springboot.desarrolloweb.entity;

import java.time.LocalDateTime;
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
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@DynamicInsert
@DynamicUpdate
public class venta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idventa;
    @Column(name = "fecha")
    private LocalDateTime fecha;
    @Column(name = "cantidad")
    private double cantidad;
    @Column(name = "total")
    private double total;
    @Column(name = "estado")
    private String estado;
    @ManyToOne
    @JoinColumn(name = "idusuario", referencedColumnName = "idusuario", foreignKey = @ForeignKey(name = "FK_usuario_venta"))
    private usuario idusuario;

}
