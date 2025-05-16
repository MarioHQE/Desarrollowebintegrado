package com.springboot.desarrolloweb.service.user;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface userservice {
    public ResponseEntity<String> signup(Map<String, String> user);

    public ResponseEntity<String> login(Map<String, String> user);

    public ResponseEntity<String> logout(Map<String, String> user);

}
