package com.springboot.desarrolloweb.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.springboot.desarrolloweb.DTO.userDTO;
import com.springboot.desarrolloweb.request.usuario.usuarioacturequest;
import com.springboot.desarrolloweb.service.user.userimpl;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api/usuario")
@Slf4j
@Tag(name = "Usuarios", description = "API para la gestión de usuarios y autenticación")
public class usuariocontroller {
        @Autowired
        private userimpl userdao;

        @Operation(summary = "Registrar nuevo usuario", description = "Registra un nuevo usuario en el sistema. El usuario recibirá un código de verificación por email.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Usuario registrado exitosamente"),
                        @ApiResponse(responseCode = "400", description = "Error en los datos de entrada o email ya existe")
        })
        @PostMapping("/signup")
        public ResponseEntity<String> signup(
                        @Parameter(description = "Datos del usuario a registrar", required = true, content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "Ejemplo de registro", value = """
                                        {
                                            "nombre": "Juan",
                                            "apellido": "Pérez",
                                            "email": "juan@example.com",
                                            "password": "password123",
                                            "telefono": "987654321"
                                        }
                                        """))) @RequestBody Map<String, String> entity) {
                return userdao.signup(entity);
        }

        @Operation(summary = "Iniciar sesión", description = "Autentica a un usuario y retorna un token JWT. El usuario debe estar verificado.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Login exitoso - Retorna token JWT", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                                        {
                                            "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
                                        }
                                        """))),
                        @ApiResponse(responseCode = "400", description = "Credenciales incorrectas"),
                        @ApiResponse(responseCode = "403", description = "Usuario no verificado")
        })
        @PostMapping("/login")
        public ResponseEntity<String> login(
                        @Parameter(description = "Credenciales del usuario", required = true, content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "Ejemplo de login", value = """
                                        {
                                            "email": "juan@example.com",
                                            "password": "password123"
                                        }
                                        """))) @RequestBody Map<String, String> entity) {
                return userdao.login(entity);
        }

        @Operation(summary = "Endpoint de prueba para administradores", description = "Endpoint protegido que requiere rol ADMIN", security = @SecurityRequirement(name = "Bearer Authentication"))
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Acceso autorizado"),
                        @ApiResponse(responseCode = "401", description = "No autorizado"),
                        @ApiResponse(responseCode = "403", description = "Acceso denegado - Se requiere rol ADMIN")
        })
        @PostMapping("/admin")
        public ResponseEntity<String> admin() {
                return new ResponseEntity<>("Hola Admin", HttpStatus.OK);
        }

        @Operation(summary = "Endpoint de prueba para usuarios", description = "Endpoint protegido que requiere rol USER", security = @SecurityRequirement(name = "Bearer Authentication"))
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Acceso autorizado"),
                        @ApiResponse(responseCode = "401", description = "No autorizado"),
                        @ApiResponse(responseCode = "403", description = "Acceso denegado - Se requiere rol USER")
        })
        @PostMapping("/user")
        public ResponseEntity<String> user() {
                return new ResponseEntity<>("Hola User", HttpStatus.OK);
        }

        @Operation(summary = "Obtener todos los usuarios", description = "Retorna una lista de todos los usuarios registrados (información básica)")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Lista de usuarios obtenida exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = userDTO.class)))
        })
        @GetMapping("/all")
        public List<userDTO> obtenerUsuarios() {
                return userdao.obtenerusuarios();
        }

        @Operation(summary = "Verificar código de verificación", description = "Verifica el código enviado por email para activar la cuenta del usuario")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Usuario verificado exitosamente"),
                        @ApiResponse(responseCode = "400", description = "Código de verificación incorrecto")
        })
        @PostMapping("/verificar")
        public ResponseEntity<String> verificationcode(
                        @Parameter(description = "Email y código de verificación", required = true, content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "Ejemplo de verificación", value = """
                                        {
                                            "email": "juan@example.com",
                                            "code": "123456"
                                        }
                                        """))) @RequestBody Map<String, String> reqMap) {
                return userdao.verificationcode(reqMap);
        }

        @PutMapping("/update/{id_usuario}")
        public ResponseEntity<String> putMethodName(@PathVariable("id_usuario") int id,
                        @Valid @RequestBody usuarioacturequest entity) {

                return userdao.actualizarperfil(entity, id);
        }
}