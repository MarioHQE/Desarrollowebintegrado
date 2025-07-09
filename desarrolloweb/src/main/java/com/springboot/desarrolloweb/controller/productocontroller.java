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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/producto")
@Tag(name = "Productos", description = "API para la gestión de productos")
public class productocontroller {

        @Autowired
        productoservice productoService;

        @Operation(summary = "Obtener todos los productos", description = "Retorna una lista de todos los productos disponibles en el sistema")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Lista de productos obtenida exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = producto.class)))
        })
        @GetMapping("/all")
        public List<producto> obtenerproductos() {
                return productoService.obtenerTodosLosProductos();
        }

        @Operation(summary = "Obtener producto por ID", description = "Retorna un producto específico basado en su ID")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Producto encontrado exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = producto.class))),
                        @ApiResponse(responseCode = "404", description = "Producto no encontrado", content = @Content)
        })
        @GetMapping("/{idProducto}")
        public producto obtenerproducto(
                        @Parameter(description = "ID del producto a buscar", required = true) @PathVariable int idProducto) {
                return productoService.obtenerProductoPorId(idProducto);
        }

        @Operation(summary = "Actualizar producto", description = "Actualiza un producto existente (Requiere rol ADMIN)", security = @SecurityRequirement(name = "Bearer Authentication"))
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Producto actualizado exitosamente"),
                        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
                        @ApiResponse(responseCode = "401", description = "No autorizado"),
                        @ApiResponse(responseCode = "403", description = "Acceso denegado - Se requiere rol ADMIN"),
                        @ApiResponse(responseCode = "404", description = "Producto no encontrado")
        })
        @PutMapping("/update/{idproducto}")
        public ResponseEntity<String> actualizarproducto(
                        @Parameter(description = "ID del producto a actualizar", required = true) @PathVariable(name = "idproducto") int idproducto,
                        @Parameter(description = "Datos del producto a actualizar", required = true) @RequestBody productoupdaterequest request) {

                return productoService.actualizarproducto(idproducto, request);
        }

        @Operation(summary = "Crear nuevo producto", description = "Crea un nuevo producto en el sistema (Requiere rol ADMIN)", security = @SecurityRequirement(name = "Bearer Authentication"))
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Producto creado exitosamente"),
                        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
                        @ApiResponse(responseCode = "401", description = "No autorizado"),
                        @ApiResponse(responseCode = "403", description = "Acceso denegado - Se requiere rol ADMIN")
        })
        @PostMapping("/save")
        public ResponseEntity<String> guardarproducto(
                        @Parameter(description = "Datos del nuevo producto", required = true) @RequestBody productorequest request) {
                return productoService.guardarproducto(request);
        }

        @Operation(summary = "Eliminar producto", description = "Elimina un producto del sistema (Requiere rol ADMIN)", security = @SecurityRequirement(name = "Bearer Authentication"))
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Producto eliminado exitosamente"),
                        @ApiResponse(responseCode = "400", description = "No se puede eliminar el producto (tiene asociaciones)"),
                        @ApiResponse(responseCode = "401", description = "No autorizado"),
                        @ApiResponse(responseCode = "403", description = "Acceso denegado - Se requiere rol ADMIN"),
                        @ApiResponse(responseCode = "404", description = "Producto no encontrado")
        })
        @DeleteMapping("/delete/{idProducto}")
        public ResponseEntity<String> eliminarproducto(
                        @Parameter(description = "ID del producto a eliminar", required = true) @PathVariable int idProducto) {
                return productoService.eliminarproducto(idProducto);
        }
}