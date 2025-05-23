package com.springboot.desarrolloweb.entity;

import java.util.Collection;
import java.util.List;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Entity
@Data
@DynamicInsert
@DynamicUpdate
@Getter
@Setter
@NamedQuery(name = "usuario.findByEmail", query = "SELECT u FROM usuario u WHERE u.email = :email")
public class usuario implements UserDetails {

    @Id
    @Column(name = "idusuario", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int idusuario;
    @Column(name = "nombre")
    private String nombre;
    @Column(name = "apellido")
    private String apellido;
    @Column(name = "email")
    private String email;
    @Column(name = "dni")
    private String dni;
    @Column(name = "ciudad")
    private String ciudad;
    @Column(name = "password")
    private String password;
    @Column(name = "enabled")
    private boolean enabled;
    @Column(name = "telefono")
    private String telefono;
    @Column(name = "verification_code", nullable = true)
    private String verificationCode;
    @OneToMany(mappedBy = "usuario", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<usuariorol> usuariorol;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return usuariorol.stream().map(rol -> (GrantedAuthority) () -> rol.getRol().getNombre()).toList();
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

}
