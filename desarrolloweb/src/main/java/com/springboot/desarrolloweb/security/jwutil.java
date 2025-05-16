package com.springboot.desarrolloweb.security;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.springboot.desarrolloweb.DTO.userDTO;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class jwutil {
    String keystr = "Mi llave Secreta de muchos digitos";
    SecretKey key = Keys.hmacShaKeyFor(keystr.getBytes(StandardCharsets.UTF_8));

    public String createtoken(UserDetails user, userDTO userdto) {
        String token = Jwts.builder().claims().subject(user.getUsername()).add("rol", user.getAuthorities())
                .add("email", userdto.getEmail()).add("telefono", userdto.getTelefono())
                .issuedAt(new Date()).expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)).and()
                .signWith(key).compact();
        return token;
    }

    public String getUser(String token) {
        String user = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload().getSubject();
        return user;
    }

}
