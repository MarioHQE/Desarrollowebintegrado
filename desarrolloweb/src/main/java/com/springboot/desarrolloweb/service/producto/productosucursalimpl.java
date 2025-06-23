package com.springboot.desarrolloweb.service.producto;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.springboot.desarrolloweb.dao.productorepository;
import com.springboot.desarrolloweb.dao.productosucursalrepository;
import com.springboot.desarrolloweb.dao.sucursalrepository;
import com.springboot.desarrolloweb.entity.ProductoSucursal;
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

        if (productosucursalexistente.getPedidoProducto().size() > 0) {
            return new ResponseEntity<>("No se puede eliminar el producto por sucursal, ya que tiene pedidos asociados",
                    HttpStatus.BAD_REQUEST);
        }
        if (productosucursalexistente.getPedidoProducto().stream()
                .anyMatch(t -> t.getPedido().getEstado().equals("PENDIENTE"))) {
            return new ResponseEntity<>(
                    "No se puede eliminar el producto por sucursal, ya que tiene pedidos pendientes asociados",
                    HttpStatus.BAD_REQUEST);
        }
        log.info("Producto por sucursal eliminado: " + productosucursalexistente.getIdProductoSucursal());
        productosucursaldoa.deleteById(productosucursalexistente.getIdProductoSucursal());
        return ResponseEntity.ok("Producto por sucursal eliminado correctamente");
    }

    @Override
    public List<ProductoSucursal> obtenerproductossucursal() {
        return productosucursaldoa.findAll();
    }
}
