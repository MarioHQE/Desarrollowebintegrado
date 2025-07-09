package com.springboot.desarrolloweb.entity;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQuery;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@DynamicInsert
@DynamicUpdate
@AllArgsConstructor
@NoArgsConstructor
@NamedQuery(name = "ubicacion_usuario.findbyusuario", query = "select u from ubicacion_usuario u where u.usuario.email = :email")
public class ubicacion_usuario {

    @Id
    @Column(name = "idubicacion_usuario")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idubicacion_usuario;
    @ManyToOne
    @JoinColumn(name = "idusuario", referencedColumnName = "idusuario", foreignKey = @ForeignKey(name = "fk_ubicacion_usuario"))
    private usuario usuario;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "idubicacion", referencedColumnName = "idubicacion", foreignKey = @ForeignKey(name = "fk_ubicacion"))
    private ubicacion ubicacion;

}
