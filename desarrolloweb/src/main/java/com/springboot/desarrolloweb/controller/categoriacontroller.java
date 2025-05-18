package com.springboot.desarrolloweb.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.springboot.desarrolloweb.entity.categoria;
import com.springboot.desarrolloweb.request.categoria.categoriarequest;
import com.springboot.desarrolloweb.request.categoria.categoriaupdaterequest;
import com.springboot.desarrolloweb.service.categoria.categoriaimpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.method.P;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/categoria")
public class categoriacontroller {
    @Autowired
    private categoriaimpl categoriaimpl;

    @GetMapping("/all")
    public List<categoria> categorias() {
        return categoriaimpl.obtenercategorias();
    }

    @GetMapping("/{idcategoria}")
    public categoria getMethodName(@PathVariable("idcategoria") int idCategoria) {
        return categoriaimpl.obtenercategoria(idCategoria);
    }

    @PostMapping("/save")
    public ResponseEntity<String> guardar(@RequestBody categoriarequest request) {

        return categoriaimpl.crearcategoria(request);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<String> actualizar(@PathVariable("idcategoria") int idcategoria,
            @RequestBody categoriaupdaterequest request) {

        return categoriaimpl.actualizarcategoria(idcategoria, request);
    }

    @DeleteMapping
    public ResponseEntity<String> eliminar(@RequestParam("idcategoria") int idcategoria) {
        return categoriaimpl.eliminarcategoria(idcategoria);
    }

}
