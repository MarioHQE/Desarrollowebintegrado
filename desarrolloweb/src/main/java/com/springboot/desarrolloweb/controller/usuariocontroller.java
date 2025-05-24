package com.springboot.desarrolloweb.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.springboot.desarrolloweb.DTO.userDTO;
import com.springboot.desarrolloweb.mappers.UsuarioMapper;
import com.springboot.desarrolloweb.service.user.userimpl;

import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/api/usuario")
@Slf4j
public class usuariocontroller {
    @Autowired
    private userimpl userdao;
    @Autowired
    private UsuarioMapper mapper;

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody Map<String, String> entity) {
        return userdao.signup(entity);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody Map<String, String> entity) {
        return userdao.login(entity);
    }

    @PostMapping("/admin")
    public ResponseEntity<String> admin() {

        return new ResponseEntity<>("Hola Admin", HttpStatus.OK);
    }

    @PostMapping("/user")
    public ResponseEntity<String> user() {

        return new ResponseEntity<>("Hola User", HttpStatus.OK);
    }

    @GetMapping("/all")
    public List<userDTO> obtenerUsuarios() {
        return userdao.obtenerusuarios();
    }

    @PostMapping("/verificar")
    public ResponseEntity<String> verificationcode(@RequestBody Map<String, String> reqMap) {

        return userdao.verificationcode(reqMap);
    }

}