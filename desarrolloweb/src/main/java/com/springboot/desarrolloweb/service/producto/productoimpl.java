package com.springboot.desarrolloweb.service.producto;

import java.time.LocalDateTime;
import java.time.ZoneId;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.springboot.desarrolloweb.dao.categoriarepository;
import com.springboot.desarrolloweb.dao.productorepository;
import com.springboot.desarrolloweb.dao.productosucursalrepository;
import com.springboot.desarrolloweb.entity.ProductoSucursal;
import com.springboot.desarrolloweb.entity.categoria;
import com.springboot.desarrolloweb.entity.producto;
import com.springboot.desarrolloweb.request.producto.productorequest;
import com.springboot.desarrolloweb.request.producto.productoupdaterequest;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class productoimpl implements productoservice {
    @Autowired
    private productorepository productodao;

    @Autowired
    private categoriarepository categoriarepository;
    @Autowired
    private productosucursalrepository productosucursaldao;

    @Override
    public List<producto> obtenerTodosLosProductos() {
        return productodao.findAll();
    }

    public producto obtenerProductoPorId(int idProducto) {
        return productodao.findById(idProducto).orElseThrow(
                () -> new RuntimeException("No se encontró el producto con ID: " + idProducto));
    }

    @Override
    @Transactional
    public ResponseEntity<String> guardarproducto(productorequest request) {
        producto producto1 = new producto();
        producto1.setNombre(request.getNombre());
        producto1.setDescripcion(request.getDescripcion());
        producto1.setPrecio(request.getPrecio());
        producto1.setImagen(request.getImagen());
        producto1.setEstado(request.getEstado());

        categoria categoriaEntity = categoriarepository.findById(request.getIdcategoria())
                .orElseThrow(
                        () -> new RuntimeException("No se encontró la categoría con ID: " + request.getIdcategoria()));

        producto1.setCategoria(categoriaEntity);

        productodao.save(producto1);
        return ResponseEntity.ok("Producto guardado correctamente");
    }

    @Override
    @Transactional
    public ResponseEntity<String> actualizarproducto(int idProducto, productoupdaterequest request) {
        producto productoexistente = productodao.findById(idProducto)
                .orElseThrow(() -> new RuntimeException("No se encontró el producto con ID: " + idProducto));
        if (request.getNombre() != null) {
            productoexistente.setNombre(request.getNombre());
        }
        if (request.getDescripcion() != null) {
            productoexistente.setDescripcion(request.getDescripcion());
        }
        if (request.getPrecio() != null) {
            productoexistente.setPrecio(request.getPrecio());
        }
        if (request.getImagen() != null) {
            productoexistente.setImagen(request.getImagen());
        }
        if (request.getEstado() != null) {
            productoexistente.setEstado(request.getEstado());
        }
        if (request.getIdcategoria() != null) {
            categoria categoriaEntity = categoriarepository.findById(request.getIdcategoria())
                    .orElseThrow(() -> new RuntimeException("No se encontró la categoría con ID: " + idProducto));
            productoexistente.setCategoria(categoriaEntity);
        }

        productodao.save(productoexistente);
        return ResponseEntity.ok("Producto actualizado correctamente");

    }

    @Override
    @Transactional
    public ResponseEntity<String> eliminarproducto(int idProducto) {
        producto productoexistente = productodao.findById(idProducto)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        // Obtener estados de pedidos
        List<String> estados = productoexistente.getProductoSucursal().stream()
                .flatMap(ps -> ps.getPedidoProducto().stream())
                .map(pedidoprod -> pedidoprod.getPedido().getEstado())
                .distinct()
                .collect(Collectors.toList());

        log.info("Producto {}: Estados encontrados: {}", idProducto, estados);

        // CASO 1: Sin pedidos - Eliminación física
        if (estados.isEmpty()) {
            List<ProductoSucursal> productosucursal = productoexistente.getProductoSucursal();
            productosucursal.forEach(productoSucursal -> productosucursaldao.delete(productoSucursal));
            productodao.deleteById(idProducto);
            return ResponseEntity.ok("✅ Producto eliminado físicamente correctamente");
        }

        // Definir conjuntos de estados
        Set<String> estadosActivos = Set.of("PENDIENTE", "PAGADO", "PREPARANDO", "LISTO_PARA_RECOGER");
        Set<String> estadosCompletados = Set.of("ENTREGADO", "CANCELADO"); // ✅ Definir aquí para usar en verificaciones

        // CASO 2: Verificar pedidos activos (PRIORIDAD MÁXIMA - NO eliminar)
        boolean tienePedidosActivos = estados.stream().anyMatch(estadosActivos::contains);
        if (tienePedidosActivos) {
            return estadosactivos(estados, productoexistente, idProducto, estadosActivos);
        }

        // CASO 3: Verificar pedidos completados (eliminación lógica)
        // ✅ CORRECCIÓN: Verificar contra estadosCompletados, no estadosActivos
        boolean tienePedidosCompletados = estados.stream().anyMatch(estadosCompletados::contains);
        if (tienePedidosCompletados) {
            return estadoscompletados(estados, productoexistente, idProducto, estadosCompletados); // ✅ AGREGAR RETURN
        }

        // CASO 4: Estados no reconocidos
        // ✅ CORRECCIÓN: Filtrar correctamente estados no reconocidos
        List<String> estadosNoReconocidos = estados.stream()
                .filter(estado -> !estadosActivos.contains(estado) && !estadosCompletados.contains(estado))
                .collect(Collectors.toList());

        if (!estadosNoReconocidos.isEmpty()) {
            return estadosnoreconocidos(estadosNoReconocidos, estadosActivos, estadosCompletados, idProducto);
        }

        // CASO 5: Fallback (no debería llegar aquí)
        log.error("Caso no contemplado para producto {}, estados: {}", idProducto, estados);
        return ResponseEntity.badRequest()
                .body("❌ Error interno: caso de eliminación no contemplado");
    }

    private ResponseEntity<String> estadosactivos(List<String> estados, producto productoexistente, int idProducto,
            Set<String> estadosactivos) {

        List<String> estadosActivosEncontrados = estados.stream()
                .filter(estadosactivos::contains)
                .collect(Collectors.toList());
        long cantidaddepedidosactivos = productoexistente.getProductoSucursal().stream().flatMap(prodsuc -> prodsuc
                .getPedidoProducto().stream().map(pedidoprod -> pedidoprod.getPedido().getEstado())
                .filter(estadosactivos::contains))
                .count();

        log.warn("Intento de eliminar producto {} con pedidos activos: {}",
                idProducto, estadosActivosEncontrados);

        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(String.format("❌ No se puede eliminar el producto. " +
                        "Tiene %d pedidos activos con estados: %s",
                        cantidaddepedidosactivos,
                        String.join(", ", estadosActivosEncontrados)));

    }

    private ResponseEntity<String> estadoscompletados(List<String> estados, producto productoexistente,
            int idProducto, Set<String> estadosCompletados) {
        List<String> estadosCompletadosEncontrados = estados.stream()
                .filter(estadosCompletados::contains)
                .collect(Collectors.toList());

        long cantidadPedidosCompletados = productoexistente.getProductoSucursal().stream()
                .flatMap(ps -> ps.getPedidoProducto().stream())
                .map(pp -> pp.getPedido().getEstado())
                .filter(estadosCompletados::contains)
                .count();

        // Aplicar eliminación lógica
        eliminadologico(productoexistente);
        productodao.save(productoexistente);

        log.info("Producto {} eliminado lógicamente con {} pedidos completados: {}",
                idProducto, cantidadPedidosCompletados, estadosCompletadosEncontrados);

        return ResponseEntity.ok(String.format(
                "✅ Producto eliminado lógicamente. Historial preservado (%d pedidos completados)",
                cantidadPedidosCompletados));
    }

    private ResponseEntity<String> estadosnoreconocidos(List<String> estadosNoReconocidos,
            Set<String> estadosActivos,
            Set<String> estadosCompletados,
            int idProducto) {
        log.error("Estados no reconocidos en producto {}: {}", idProducto, estadosNoReconocidos);
        return ResponseEntity.badRequest()
                .body("❌ Estados de pedidos no reconocidos: " + String.join(", ", estadosNoReconocidos));
    }

    private void eliminadologico(producto productoexistente) {
        productoexistente.setEliminado(true);
        productoexistente.setEstado(false);
        productoexistente.setFechaEliminacion(LocalDateTime.now(ZoneId.of("America/Lima")));

    }

}
