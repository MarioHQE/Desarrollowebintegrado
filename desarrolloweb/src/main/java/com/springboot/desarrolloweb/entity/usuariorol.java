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
import jakarta.persistence.Table;
import lombok.Data;

@Table(name = "usuario_rol")
@DynamicInsert
@DynamicUpdate
@Entity
@Data
public class usuariorol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idusuariorol")
    private int idusuariorol;

    @ManyToOne
    @JoinColumn(name = "idusuario", foreignKey = @ForeignKey(name = "fk_usuario"), referencedColumnName = "idusuario")
    private usuario usuario;

    @ManyToOne
    @JoinColumn(name = "idrol", foreignKey = @ForeignKey(name = "fk_rol"), referencedColumnName = "idrol")
    private rol rol;

}
