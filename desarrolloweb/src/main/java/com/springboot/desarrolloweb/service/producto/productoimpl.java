package com.springboot.desarrolloweb.service.producto;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.springboot.desarrolloweb.dao.categoriarepository;
import com.springboot.desarrolloweb.dao.productorepository;
import com.springboot.desarrolloweb.dao.productosucursalrepository;
import com.springboot.desarrolloweb.dao.sucursalrepository;
import com.springboot.desarrolloweb.entity.ProductoSucursal;
import com.springboot.desarrolloweb.entity.categoria;
import com.springboot.desarrolloweb.entity.producto;
import com.springboot.desarrolloweb.request.producto.productorequest;
import com.springboot.desarrolloweb.request.producto.productosucursalrequest;
import com.springboot.desarrolloweb.request.producto.productosucursalupdaterequest;
import com.springboot.desarrolloweb.request.producto.productoupdaterequest;

@Service
public class productoimpl implements productoservice {
    @Autowired
    private productorepository productodao;
    @Autowired
    private productosucursalrepository productosucursaldoa;
    @Autowired
    private sucursalrepository sucursaldao;
    @Autowired
    private categoriarepository categoriarepository;

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
    public ResponseEntity<String> eliminarproducto(int idProducto) {
        producto productoexistente = productodao.findById(idProducto)
                .orElseThrow(() -> new RuntimeException("No se encontró el producto con ID: " + idProducto));
        if (productoexistente.getProductoSucursal().size() > 0) {
            return new ResponseEntity<>(
                    "No se puede eliminar el producto, ya que tiene productos por sucursal asociados",
                    HttpStatus.BAD_REQUEST);

        }
        return ResponseEntity.ok("Producto eliminado correctamente");

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

    // productosucursal
    @Override
    public List<ProductoSucursal> obtenerProductosPorSucursal(int idSucursal) {
        return productosucursaldoa.findProductosBySucursal(idSucursal);

    }

    @Override
    @Transactional
    public ResponseEntity<String> actualizarStock(int idProducto, int idSucursal, int stock) {
        ProductoSucursal productoSucursal = productosucursaldoa.findbyproductoysucursal(idProducto, idSucursal)
                .orElseThrow(() -> new RuntimeException("No se encuentra el producto por sucursal"));
        productoSucursal.setStock(stock);
        productosucursaldoa.save(productoSucursal);
        return ResponseEntity.ok("Stock actualizado correctamente");

    }

    @Override
    public Integer disminuirStock(int idProducto, int idSucursal, int stockdisminuido) {
        ProductoSucursal productoSucursal = productosucursaldoa.findbyproductoysucursal(idProducto, idSucursal)
                .orElseThrow(() -> new RuntimeException("No se encuentra el producto por sucursal"));
        Integer stockActualizado = productoSucursal.getStock() - stockdisminuido;
        productoSucursal.setStock(stockActualizado);
        productosucursaldoa.save(productoSucursal);
        return stockActualizado;
    }

    @Override
    public Integer aumentarStock(int idProducto, int idSucursal, int stockaumentado) {
        ProductoSucursal productoSucursal = productosucursaldoa.findbyproductoysucursal(idProducto, idSucursal)
                .orElseThrow(() -> new RuntimeException("No se encuentra el producto por sucursal"));
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
        ProductoSucursal productosucursalexistente = productosucursaldoa.findbyproductoysucursal(idProducto, idSucursal)
                .orElseThrow(() -> new RuntimeException("No se encuentra el producto por sucursal"));
        if (productosucursalexistente.getPedidoProducto().size() > 0) {
            return new ResponseEntity<>("No se puede eliminar el producto por sucursal, ya que tiene pedidos asociados",
                    HttpStatus.BAD_REQUEST);

        }
        productosucursaldoa.delete(productosucursalexistente);
        return ResponseEntity.ok("Producto por sucursal eliminado correctamente");
    }

}
