package com.springboot.desarrolloweb.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.springboot.desarrolloweb.entity.ProductoSucursal;
import com.springboot.desarrolloweb.request.producto.productosucursalrequest;
import com.springboot.desarrolloweb.request.producto.productosucursalupdaterequest;
import com.springboot.desarrolloweb.service.producto.productoservice;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/productosucursal")
public class productosucursalcontroller {

    @Autowired
    private productoservice productoService;

    /**
     * Obtiene todos los productos de una sucursal específica
     * 
     * @param idSucursal ID de la sucursal
     * @return Lista de productos en la sucursal
     */
    @GetMapping("/sucursal/{idSucursal}")
    public List<ProductoSucursal> obtenerProductosPorSucursal(@PathVariable("idSucursal") int idSucursal) {
        return productoService.obtenerProductosPorSucursal(idSucursal);
    }

    /**
     * Agrega un producto a una sucursal
     * 
     * @param request Datos del producto y sucursal
     * @return Mensaje de confirmación
     */
    @PostMapping("/agregar")
    public ResponseEntity<String> agregarProductoSucursal(@Valid @RequestBody productosucursalrequest request) {
        return productoService.agregarProductoSucursal(request);
    }

    /**
     * Actualiza la información de un producto en una sucursal
     * 
     * @param request Datos actualizados
     * @return Mensaje de confirmación
     */
    @PutMapping("/actualizar")
    public ResponseEntity<String> actualizarProductoSucursal(
            @Valid @RequestBody productosucursalupdaterequest request) {
        return productoService.actualizarProductoSucursal(request);
    }

    /**
     * Elimina un producto de una sucursal
     * 
     * @param idProducto ID del producto
     * @param idSucursal ID de la sucursal
     * @return Mensaje de confirmación
     */
    @DeleteMapping("/eliminar")
    public ResponseEntity<String> eliminarProductoSucursal(@RequestParam int idProducto, @RequestParam int idSucursal) {
        return productoService.eliminarProductoSucursal(idProducto, idSucursal);
    }

    /**
     * Actualiza el stock de un producto en una sucursal
     * 
     * @param idProducto ID del producto
     * @param idSucursal ID de la sucursal
     * @param stock      Nuevo valor de stock
     * @return Mensaje de confirmación
     */
    @PutMapping("/stock/actualizar")
    public ResponseEntity<String> actualizarStock(
            @RequestParam int idProducto,
            @RequestParam int idSucursal,
            @RequestParam int stock) {
        return productoService.actualizarStock(idProducto, idSucursal, stock);
    }

    /**
     * Obtiene el stock actual de un producto en una sucursal específica
     * 
     * @param idProducto ID del producto
     * @param idSucursal ID de la sucursal
     * @return Valor del stock actual
     */
    @GetMapping("/stock")
    public ResponseEntity<Integer> obtenerStock(
            @RequestParam int idProducto,
            @RequestParam int idSucursal) {
        try {
            Integer stock = productoService.disminuirStock(idProducto, idSucursal, 0);
            return ResponseEntity.ok(stock);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Disminuye el stock de un producto en una sucursal
     * 
     * @param idProducto ID del producto
     * @param idSucursal ID de la sucursal
     * @param cantidad   Cantidad a disminuir
     * @return Stock actualizado
     */
    @PutMapping("/stock/disminuir")
    public Integer disminuirStock(
            @RequestParam int idProducto,
            @RequestParam int idSucursal,
            @RequestParam int cantidad) {
        return productoService.disminuirStock(idProducto, idSucursal, cantidad);
    }

    /**
     * Aumenta el stock de un producto en una sucursal
     * 
     * @param idProducto ID del producto
     * @param idSucursal ID de la sucursal
     * @param cantidad   Cantidad a aumentar
     * @return Stock actualizado
     */
    @PutMapping("/stock/aumentar")
    public Integer aumentarStock(
            @RequestParam int idProducto,
            @RequestParam int idSucursal,
            @RequestParam int cantidad) {
        return productoService.aumentarStock(idProducto, idSucursal, cantidad);
    }
}