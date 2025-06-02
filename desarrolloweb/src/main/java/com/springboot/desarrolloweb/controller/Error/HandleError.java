package com.springboot.desarrolloweb.controller.Error;

import java.util.stream.Collectors;

import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import jakarta.validation.ConstraintViolationException;

@ControllerAdvice
public class HandleError {

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<String> handlevalidationexception(ConstraintViolationException e) {
        String errores = e.getConstraintViolations().stream().map((c) -> c.getMessage())
                .collect(Collectors.joining(" | "));
        return ResponseEntity.badRequest().body("Error: " + errores);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<String> handleNotFound(NotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());

    }

}