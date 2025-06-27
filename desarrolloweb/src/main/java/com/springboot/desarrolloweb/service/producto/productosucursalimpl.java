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

import com.springboot.desarrolloweb.dao.productorepository;
import com.springboot.desarrolloweb.dao.productosucursalrepository;
import com.springboot.desarrolloweb.dao.sucursalrepository;
import com.springboot.desarrolloweb.entity.ProductoSucursal;
import com.springboot.desarrolloweb.entity.producto;
import com.springboot.desarrolloweb.entity.ProductoSucursal.estado;
import com.springboot.desarrolloweb.request.producto.productosucursalrequest;
import com.springboot.desarrolloweb.request.producto.productosucursalupdaterequest;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class productosucursalimpl implements productosucursalservice {
    @Autowired
    private productorepository productodao;
    @Autowired
    private productosucursalrepository productosucursaldoa;
    @Autowired
    private sucursalrepository sucursaldao;
    Set<String> estadosActivos = Set.of("PENDIENTE", "PAGADO", "PREPARANDO", "LISTO_PARA_RECOGER");
    Set<String> estadosCompletados = Set.of("ENTREGADO", "CANCELADO");

    @Override
    public List<ProductoSucursal> obtenerProductosPorSucursal(int idSucursal) {
        return productosucursaldoa.findProductosBySucursal(idSucursal);

    }

    @Override
    @Transactional
    public ResponseEntity<String> actualizarStock(int idProducto, int idSucursal, int stock) {
        ProductoSucursal productoSucursal = productosucursaldoa.findbyproductoysucursal(idProducto, idSucursal);
        productoSucursal.setStock(stock);
        productosucursaldoa.save(productoSucursal);
        return ResponseEntity.ok("Stock actualizado correctamente");

    }

    @Override
    public Integer disminuirStock(int idProducto, int idSucursal, int stockdisminuido) {
        ProductoSucursal productoSucursal = productosucursaldoa.findbyproductoysucursal(idProducto, idSucursal);
        Integer stockActualizado = productoSucursal.getStock() - stockdisminuido;
        productoSucursal.setStock(stockActualizado);
        productosucursaldoa.save(productoSucursal);
        return stockActualizado;
    }

    @Override
    public Integer aumentarStock(int idProducto, int idSucursal, int stockaumentado) {
        ProductoSucursal productoSucursal = productosucursaldoa.findbyproductoysucursal(idProducto, idSucursal);
        Integer stockActualizado = productoSucursal.getStock() + stockaumentado;
        productoSucursal.setStock(stockActualizado);
        productosucursaldoa.save(productoSucursal);
        return stockActualizado;
    }

    @Override
    public ResponseEntity<String> agregarProductoSucursal(productosucursalrequest productosucursalrequest) {
        ProductoSucursal productoSucursal = new ProductoSucursal();
        producto producto = productodao.findById(productosucursalrequest.getProducto()).orElseThrow();
        boolean ya_existe = productosucursaldoa.findbyproductoysucursal(productosucursalrequest.getProducto(),
                productosucursalrequest.getSucursal()) != null ? true : false;
        ProductoSucursal productoSucursalExistente = productosucursaldoa.findbyproductoysucursal(
                productosucursalrequest.getProducto(),
                productosucursalrequest.getSucursal());
        if (ya_existe) {
            if (!productoSucursalExistente.isEliminado()) {
                return new ResponseEntity<>("El producto ya existe y esta activo", HttpStatus.BAD_REQUEST);
            } else {
                productoSucursalExistente.setEliminado(false);
                productoSucursalExistente.setActivo(true);
                productoSucursalExistente.setEstado(ProductoSucursal.estado.ACTIVO);
                productoSucursalExistente.setStock(productosucursalrequest.getStock());
                productoSucursalExistente.setFechaEliminacion(null);
                productoSucursalExistente.setMotivoEliminacion(null);
                productosucursaldoa.save(productoSucursalExistente);
                return ResponseEntity.ok("Producto reactivado denuevo");
            }
        }

        productoSucursal.setStock(productosucursalrequest.getStock());
        productoSucursal.setProducto(productodao.findById(productosucursalrequest.getProducto()).get());
        productoSucursal.setSucursal(sucursaldao.findById(productosucursalrequest.getSucursal()).get());
        productosucursaldoa.save(productoSucursal);
        return ResponseEntity.ok("Producto agregado correctamente");

    }

    @Override
    @Transactional
    public ResponseEntity<String> actualizarProductoSucursal(productosucursalupdaterequest productosucursalrequest) {
        ProductoSucursal productoSucursal = productosucursaldoa
                .findById(productosucursalrequest.getIdproductosucursal())
                .orElseThrow(() -> new RuntimeException("No se encuentra el producto por sucursal"));
        if (productosucursalrequest.getStock() != null) {
            productoSucursal.setStock(productosucursalrequest.getStock());
        }
        if (productosucursalrequest.getProducto() != null) {
            productoSucursal.setProducto(productodao.findById(productosucursalrequest.getProducto()).get());
        }
        if (productosucursalrequest.getSucursal() != null) {
            productoSucursal.setSucursal(sucursaldao.findById(productosucursalrequest.getSucursal()).get());
        }
        try {
            productosucursaldoa.save(productoSucursal);
        } catch (Exception e) {
            return new ResponseEntity<>("Error al actualizar el producto por sucursal", HttpStatus.BAD_REQUEST);

        }
        return new ResponseEntity<>("Producto actualizado correctamente", HttpStatus.OK);
    }

    @Override
    @Transactional
    public ResponseEntity<String> eliminarProductoSucursal(int idProducto, int idSucursal) {
        ProductoSucursal productosucursalexistente = productosucursaldoa.findbyproductoysucursal(idProducto,
                idSucursal);

        if (productosucursalexistente == null) {
            return new ResponseEntity<>("No se encuentra el producto por sucursal", HttpStatus.NOT_FOUND);
        }
        if (productosucursalexistente.isEliminado()) {
            return new ResponseEntity<>("El producto por sucursal ya fue eliminado", HttpStatus.BAD_REQUEST);
        }

        boolean tienepedidos = productosucursalexistente.getPedidoProducto() != null
                && productosucursalexistente.getPedidoProducto().size() > 0;
        if (tienepedidos) {

            Set<String> EstadosEncontrados = productosucursalexistente.getPedidoProducto()
                    .stream()
                    .map(pp -> pp.getPedido().getEstado())
                    .collect(Collectors.toSet());
            boolean tieneAlgunPedidoActivo = EstadosEncontrados.stream()
                    .anyMatch(estadosActivos::contains);
            if (tieneAlgunPedidoActivo) {
                return estadoactivosevent(productosucursalexistente, EstadosEncontrados);
            }
            boolean todosPedidosCompletados = EstadosEncontrados.stream()
                    .allMatch(estadosCompletados::contains);
            if (todosPedidosCompletados) {
                return estadocompletadoevent(productosucursalexistente, EstadosEncontrados);
            }

        } else {
            productosucursaldoa.delete(productosucursalexistente);
            return new ResponseEntity<>("Producto eliminado correctamente", HttpStatus.OK);
        }
        return new ResponseEntity<>("No se puede eliminar el producto", HttpStatus.BAD_REQUEST);

    }

    public void eliminadologico(ProductoSucursal productosucursalexistente) {
        ;
        productosucursalexistente.setActivo(false);
        productosucursalexistente.setEliminado(true);
        productosucursalexistente.setEstado(estado.ELIMINADO);
        productosucursalexistente.setFechaEliminacion(LocalDateTime.now(ZoneId.of("America/Lima")));

    }

    public ResponseEntity<String> estadoactivosevent(ProductoSucursal productoSucursal, Set<String> totalactivos) {
        int totalactivossize = totalactivos.size();
        long totalpedidos = productoSucursal.getPedidoProducto().stream().map(pedidoprod -> pedidoprod.getPedido())
                .count();
        String nombredeproducto = productoSucursal.getProducto().getNombre();
        String mensaje = String.format("El producto %s tiene %d pedidos activos y tiene %d pedidos ", nombredeproducto,
                totalactivossize, totalpedidos);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(mensaje);
    }

    public ResponseEntity<String> estadocompletadoevent(ProductoSucursal productoSucursal,
            Set<String> totalcompletados) {
        int totalcompletadossize = totalcompletados.size();
        long totalpedidos = productoSucursal.getPedidoProducto().stream().map(pedidoprod -> pedidoprod.getPedido())
                .count();
        String nombredeproducto = productoSucursal.getProducto().getNombre();
        eliminadologico(productoSucursal);
        String mensaje = String.format(
                "El producto %s se ha eliminado lógicamente. tiene %d pedidos completados y tiene %d pedidos ",
                nombredeproducto,
                totalcompletadossize, totalpedidos);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(mensaje);

    }

    public List<ProductoSucursal> obtenerproductossucursal() {
        return productosucursaldoa.findAll();
    }
}
