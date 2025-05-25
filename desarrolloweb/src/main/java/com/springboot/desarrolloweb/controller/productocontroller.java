package com.springboot.desarrolloweb.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.springboot.desarrolloweb.entity.producto;
import com.springboot.desarrolloweb.request.producto.productorequest;
import com.springboot.desarrolloweb.request.producto.productoupdaterequest;
import com.springboot.desarrolloweb.service.producto.productoservice;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/producto")
public class productocontroller {

    @Autowired
    productoservice productoService;

    @GetMapping("/all")
    public List<producto> obtenerproductos() {
        return productoService.obtenerTodosLosProductos();
    }

    @GetMapping("/{idProducto}")
    public producto obtenerproducto(@PathVariable int idProducto) {
        return productoService.obtenerProductoPorId(idProducto);
    }

    @PutMapping("/update/{idproducto}")
    public ResponseEntity<String> actualizarproducto(@PathVariable(name = "idproducto") int idproducto,
            @RequestBody productoupdaterequest request) {

        return productoService.actualizarproducto(idproducto, request);
    }

    @PostMapping("/save")
    public ResponseEntity<String> guardarproducto(@RequestBody productorequest request) {
        return productoService.guardarproducto(request);
    }

    @DeleteMapping("/delete/{idProducto}")
    public ResponseEntity<String> eliminarproducto(@PathVariable int idProducto) {
        return productoService.eliminarproducto(idProducto);
    }
}
