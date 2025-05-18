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

    List<ProductoSucursal> obtenerProductosPorSucursal(int idSucursal);

    ResponseEntity<String> guardarproducto(productorequest producto);

    ResponseEntity<String> eliminarproducto(int idProducto);

    ResponseEntity<String> actualizarproducto(int idProducto, productoupdaterequest request);

    ResponseEntity<String> agregarProductoSucursal(productosucursalrequest productoSucursal);

    ResponseEntity<String> actualizarProductoSucursal(productosucursalupdaterequest productoSucursal);

    ResponseEntity<String> eliminarProductoSucursal(int idProducto, int idSucursal);

    ResponseEntity<String> actualizarStock(int idProducto, int idSucursal, int stock);

    Integer disminuirStock(int idProducto, int idSucursal, int stock);

    Integer aumentarStock(int idProducto, int idSucursal, int stock);

}
