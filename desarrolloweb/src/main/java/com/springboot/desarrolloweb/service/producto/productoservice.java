package com.springboot.desarrolloweb.service.producto;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.springboot.desarrolloweb.entity.ProductoSucursal;
import com.springboot.desarrolloweb.entity.producto;
import com.springboot.desarrolloweb.request.producto.productorequest;
import com.springboot.desarrolloweb.request.producto.productosucursalrequest;
import com.springboot.desarrolloweb.request.producto.productosucursalupdaterequest;
import com.springboot.desarrolloweb.request.producto.productoupdaterequest;

@Service
public interface productoservice {
    producto obtenerProductoPorId(int idProducto);

    List<producto> obtenerTodosLosProductos();

    ResponseEntity<String> guardarproducto(productorequest productorequest);

    ResponseEntity<String> eliminarproducto(int idProducto);

    ResponseEntity<String> actualizarproducto(int idProducto, productoupdaterequest request);

}
