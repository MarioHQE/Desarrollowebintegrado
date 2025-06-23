package com.springboot.desarrolloweb.service.producto;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.springboot.desarrolloweb.entity.ProductoSucursal;
import com.springboot.desarrolloweb.request.producto.productosucursalrequest;
import com.springboot.desarrolloweb.request.producto.productosucursalupdaterequest;

@Service
public interface productosucursalservice {
    List<ProductoSucursal> obtenerproductossucursal();

    List<ProductoSucursal> obtenerProductosPorSucursal(int idSucursal);

    ResponseEntity<String> agregarProductoSucursal(productosucursalrequest productoSucursal);

    ResponseEntity<String> actualizarProductoSucursal(productosucursalupdaterequest productoSucursal);

    ResponseEntity<String> eliminarProductoSucursal(int idProducto, int idSucursal);

    ResponseEntity<String> actualizarStock(int idProducto, int idSucursal, int stock);

    Integer disminuirStock(int idProducto, int idSucursal, int stock);

    Integer aumentarStock(int idProducto, int idSucursal, int stock);
}
