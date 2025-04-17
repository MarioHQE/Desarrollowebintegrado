package com.springboot.desarrolloweb.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.springboot.desarrolloweb.service.userimpl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/usuario")
public class usuariocontroller {
    @Autowired
    private userimpl userdao;

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody Map<String, String> entity) {
        // TODO: process POST request
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

}