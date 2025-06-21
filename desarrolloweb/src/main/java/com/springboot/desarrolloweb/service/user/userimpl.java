package com.springboot.desarrolloweb.service.user;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.springboot.desarrolloweb.DTO.userDTO;
import com.springboot.desarrolloweb.dao.rolrepository;
import com.springboot.desarrolloweb.dao.usuariorepository;
import com.springboot.desarrolloweb.dao.usuariorolrepository;
import com.springboot.desarrolloweb.entity.rol;
import com.springboot.desarrolloweb.entity.usuario;
import com.springboot.desarrolloweb.entity.usuariorol;
import com.springboot.desarrolloweb.mappers.UsuarioMapper;
import com.springboot.desarrolloweb.request.usuario.usuarioacturequest;
import com.springboot.desarrolloweb.security.jwutil;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class userimpl implements userservice {
    @Autowired
    private UsuarioMapper mapper;
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    private usuariorepository usuariodao;
    @Autowired
    private rolrepository roldao;
    @Autowired
    private usuariorolrepository usuarioroldao;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private jwutil jwutil;

    @Override
    @Transactional
    public ResponseEntity<String> signup(Map<String, String> user) {
        user.put("password", passwordEncoder.encode(user.get("password")));
        if (!validatesignup(user)) {
            return ResponseEntity.badRequest().body("Error al registrar el usuario, no se encontro el rol");
        }

        if (usuariodao.findByEmail(user.get("email")) == null) {
            return ResponseEntity.badRequest().body("Error al registrar el usuario, el email ya existe");
        }

        traerusuario(user);

        return ResponseEntity.ok("Usuario registrado correctamente");

    }

    private String createverificationcode() {
        String code = "";
        for (int i = 0; i < 6; i++) {
            code += (int) (Math.random() * 10);
        }
        return code;

    }

    private boolean validatesignup(Map<String, String> user) {
        return user.containsKey("nombre") && user.containsKey("apellido")
                && user.containsKey("email") && user.containsKey("password")
                && user.containsKey("telefono");
    }

    public usuario traerusuario(Map<String, String> user) {
        // Buscar el rol ROLE_USER
        rol rol = roldao.findbynombre("ROLE_USER");
        rol roladmin = roldao.findbynombre("ROLE_ADMIN");
        if (rol == null) {
            // Si no existe, lo crea
            roladmin = new rol();
            roladmin.setNombre("ROLE_ADMIN");
            roldao.save(roladmin);
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
        usuario.setEnabled(false);

        // Crear la relación usuario_rol
        usuariorol ur = new usuariorol();
        ur.setUsuario(usuario);
        ur.setRol(rol);

        // Agregar el usuarioRol a la lista del usuario
        usuario.setVerificationCode(createverificationcode());
        usuario usuario2 = usuariodao.save(usuario);
        usuarioroldao.save(ur);
        return usuario2;

    }

    @Override
    public ResponseEntity<String> login(Map<String, String> user) {
        if (!validateaccount(user)) {
            return ResponseEntity.badRequest()
                    .body("Error al iniciar sesion, el usuario no existe o la contraseña es incorrecta");
        }
        usuario usuario = usuariodao.findByEmail(user.get("email"))
                .orElseThrow(() -> new RuntimeException("User not found"));
        userDTO userdto = mapper.usertoDTO(usuario);
        if (!usuario.isEnabled()) {
            enviarverificationcode(userdto);

            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Error al iniciar sesion, el usuario no se ha verificado");
        }
        String token = jwutil.createtoken(usuario, userdto);
        return new ResponseEntity<String>("{\"token\":\"" + token + "\"}",
                HttpStatus.OK);
    }

    private void enviarverificationcode(userDTO userdto) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(userdto.getEmail());
        message.setSubject("Código de verificación");
        message.setText("Tu código de verificación es: " + userdto.getVerificationCode());
        mailSender.send(message);
    }

    public boolean validateaccount(Map<String, String> user) {

        if (usuariodao.findByEmail(user.get("email")) != null && passwordEncoder.matches(user.get("password"),
                usuariodao.findByEmail(user.get("email")).orElseThrow(() -> new RuntimeException("User not found"))
                        .getPassword())) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public List<userDTO> obtenerusuarios() {
        List<usuario> users = usuariodao.findAll();

        return users.stream().map(mapper::usertoDTO).collect(Collectors.toList());

    }

    @Override
    public ResponseEntity<String> logout(Map<String, String> user) {
        return null;
    }

    @Override
    public ResponseEntity<String> verificationcode(Map<String, String> reqMap) {
        String email = reqMap.get("email");
        String codigo = reqMap.get("code");

        usuario usuario = usuariodao.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        if (!usuario.getVerificationCode().equals(codigo)) {
            return ResponseEntity.badRequest().body("El código de verificación es incorrecto");
        }
        if (usuario.isEnabled()) {
            return ResponseEntity.ok("El usuario ya se ha verificado");
        }
        usuario.setEnabled(true);
        usuariodao.save(usuario);
        return ResponseEntity.ok("El usuario se ha verificado correctamente");
    }

    @Override
    public ResponseEntity<String> actualizarperfil(usuarioacturequest usuariorequest, int id_usuario) {
        usuario usuario = usuariodao.findById(id_usuario).orElseThrow(() -> new RuntimeException("User not found"));
        if (usuariorequest.getNombre() != null) {
            usuario.setNombre(usuariorequest.getNombre().trim());
        } else if (usuariorequest.getApellido() != null) {
            usuario.setApellido(usuariorequest.getApellido());
        } else if (usuariorequest.getEmail() != null) {
            usuario.setEmail(usuariorequest.getEmail());
        } else if (usuariorequest.getTelefono() != null) {
            usuario.setTelefono(usuariorequest.getTelefono());
        } else {
            return ResponseEntity.badRequest().body("No se proporcionaron datos para actualizar");
        }

        usuariodao.save(usuario);
        return ResponseEntity.ok("El perfil se ha actualizado correctamente");
    }

}
