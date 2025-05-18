package com.springboot.desarrolloweb.service.categoria;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.springboot.desarrolloweb.dao.categoriarepository;
import com.springboot.desarrolloweb.entity.categoria;
import com.springboot.desarrolloweb.request.categoria.categoriarequest;
import com.springboot.desarrolloweb.request.categoria.categoriaupdaterequest;

import jakarta.transaction.Transactional;

@Service
public class categoriaimpl implements categoriaservice {
    @Autowired
    categoriarepository categoriarepository;

    @Override
    public List<categoria> obtenercategorias() {

        return categoriarepository.findAll();
    }

    @Override
    public categoria obtenercategoria(int idCategoria) {
        return categoriarepository.findById(idCategoria).orElseThrow(
                () -> new RuntimeException("No se encontró la categoría con ID: " + idCategoria));
    }

    @Override
    @Transactional
    public ResponseEntity<String> crearcategoria(categoriarequest categoria) {
        // TODO Auto-generated method stub
        categoria categoria1 = new categoria();
        categoria1.setName(categoria.getName());

        categoriarepository.save(categoria1);
        return ResponseEntity.ok("Categoria guardada correctamente");
    }

    @Transactional
    @Override
    public ResponseEntity<String> actualizarcategoria(int idCategoria, categoriaupdaterequest categoria) {
        // TODO Auto-generated method stub
        categoria categoriaexistente = categoriarepository.findById(idCategoria).orElseThrow(
                () -> new RuntimeException("No se encontró la categoría con ID: " + idCategoria));

        categoriaexistente.setName(categoria.getName());

        categoriarepository.save(categoriaexistente);
        return ResponseEntity.ok("Categoria actualizada correctamente");
    }

    @Override
    @Transactional
    public ResponseEntity<String> eliminarcategoria(int idCategoria) {
        categoria categoriaexistente = categoriarepository.findById(idCategoria).orElseThrow(
                () -> new RuntimeException("No se encontró la categoría con ID: " + idCategoria));
        if (categoriaexistente.getProductos().size() > 0) {
            return ResponseEntity.badRequest()
                    .body("No se puede eliminar la categoría porque tiene productos asociados.");

        }
        categoriarepository.delete(categoriaexistente);
        return ResponseEntity.ok("Categoria eliminada correctamente");
    }

}
