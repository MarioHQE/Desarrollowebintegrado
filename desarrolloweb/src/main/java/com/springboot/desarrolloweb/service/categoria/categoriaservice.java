package com.springboot.desarrolloweb.service.categoria;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.springboot.desarrolloweb.entity.categoria;
import com.springboot.desarrolloweb.request.categoria.categoriarequest;
import com.springboot.desarrolloweb.request.categoria.categoriaupdaterequest;

@Service
public interface categoriaservice {
    public List<categoria> obtenercategorias();

    public categoria obtenercategoria(int idCategoria);

    public ResponseEntity<String> crearcategoria(categoriarequest categoria);

    public ResponseEntity<String> actualizarcategoria(int idCategoria, categoriaupdaterequest categoria);

    public ResponseEntity<String> eliminarcategoria(int idCategoria);
}
