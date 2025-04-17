package com.springboot.desarrolloweb.service;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.springboot.desarrolloweb.DTO.userDTO;
import com.springboot.desarrolloweb.dao.rolrepository;
import com.springboot.desarrolloweb.dao.usuariorepository;
import com.springboot.desarrolloweb.dao.usuariorolrepository;
import com.springboot.desarrolloweb.entity.rol;
import com.springboot.desarrolloweb.entity.usuario;
import com.springboot.desarrolloweb.entity.usuariorol;
import com.springboot.desarrolloweb.security.jwutil;
import com.springboot.desarrolloweb.security.userdetailsservice;

@Service
public class userimpl implements userservice {
    @Autowired
    private usuariorepository usuariodao;
    @Autowired
    private rolrepository roldao;
    @Autowired
    private usuariorolrepository usuarioroldao;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private jwutil jwutil;
    @Autowired
    private userdetailsservice userdetailsservice;

    @Override
    public ResponseEntity<String> signup(Map<String, String> user) {
        user.put("password", passwordEncoder.encode(user.get("password")));
        if (validatesignup(user)) {
            if (usuariodao.findByEmail(user.get("email")) != null) {
                return ResponseEntity.badRequest().body("Error al registrar el usuario, el email ya existe");
            } else {
                traerusuario(user);
                return ResponseEntity.ok("Usuario registrado correctamente");
            }
        } else {
            return ResponseEntity.badRequest().body("Error al registrar el usuario, no se encontro el rol");
        }
    }

    private boolean validatesignup(Map<String, String> user) {
        return user.containsKey("nombre") && user.containsKey("apellido")
                && user.containsKey("email") && user.containsKey("password")
                && user.containsKey("telefono");
    }

    public void traerusuario(Map<String, String> user) {
        // Buscar el rol ROLE_USER
        rol rol = roldao.findbynombre("ROLE_USER");

        if (rol == null) {
            // Si no existe, lo crea
            rol = new rol();
            rol.setNombre("ROLE_USER");
            roldao.save(rol);
            System.out.println("Se ha creado un nuevo rol: " + rol.getNombre());
        }

        // Crear el nuevo usuario
        usuario usuario = new usuario();
        usuario.setNombre(user.get("nombre"));
        usuario.setApellido(user.get("apellido"));
        usuario.setEmail(user.get("email"));
        usuario.setPassword(user.get("password"));
        usuario.setTelefono(user.get("telefono"));
        usuario.setEnabled(true);

        // Crear la relación usuario_rol
        usuariorol ur = new usuariorol();
        ur.setUsuario(usuario);
        ur.setRol(rol);

        // Agregar el usuarioRol a la lista del usuario

        usuariodao.save(usuario);
        usuarioroldao.save(ur);
    }

    @Override
    public ResponseEntity<String> login(Map<String, String> user) {
        if (validateaccount(user)) {
            UserDetails usuario = userdetailsservice.loadUserByUsername(user.get("email"));
            userDTO userdto = new userDTO(usuariodao.findByEmail(user.get("email")));
            if (SecurityContextHolder.getContext().getAuthentication().isAuthenticated()) {

                if (usuario.isEnabled() == true) {
                    String token = jwutil.createtoken(usuario, userdto);
                    return new ResponseEntity<String>("{\"token\":\"" + token + "\"}",
                            HttpStatus.OK);
                } else {
                    return ResponseEntity.badRequest().body("Error al iniciar sesion, el usuario no esta habilitado");
                }
            } else {
                return ResponseEntity.ok().body("No esta autenticado");
            }

        } else {
            return ResponseEntity.badRequest()
                    .body("Error al iniciar sesion, el usuario no existe o la contraseña es incorrecta");
        }
    }

    public boolean validateaccount(Map<String, String> user) {

        if (usuariodao.findByEmail(user.get("email")) != null && passwordEncoder.matches(user.get("password"),
                usuariodao.findByEmail(user.get("email")).getPassword())) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public ResponseEntity<String> logout(Map<String, String> user) {
        return null;
    }

}
