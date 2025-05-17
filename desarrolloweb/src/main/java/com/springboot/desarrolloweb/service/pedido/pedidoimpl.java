package com.springboot.desarrolloweb.service.pedido;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

import com.springboot.desarrolloweb.dao.pedidorepository;
import com.springboot.desarrolloweb.dao.productosucursalrepository;
import com.springboot.desarrolloweb.dao.usuariorepository;
import com.springboot.desarrolloweb.entity.ProductoSucursal;
import com.springboot.desarrolloweb.entity.pedido;
import com.springboot.desarrolloweb.entity.pedidoproducto;
import com.springboot.desarrolloweb.entity.usuario;
import com.springboot.desarrolloweb.request.pedido.pedidoproductorequest;
import com.springboot.desarrolloweb.request.pedido.pedidorequest;
import com.springboot.desarrolloweb.request.pedido.pedidoupdaterequest;

public class pedidoimpl implements pedidoservice {
    @Autowired
    productosucursalrepository productosucursalrepository;
    @Autowired
    pedidorepository pedidorepository;
    @Autowired
    usuariorepository usuariorepository;

    @Override
    public List<pedido> getPedidos() {
        return pedidorepository.findAll();
    }

    @Override
    public pedido getPedido(int idPedido) {

        return pedidorepository.findById(idPedido)
                .orElseThrow(() -> new RuntimeException("No se encontró el pedido con ID: " + idPedido));
    }

    @Override
    @Transactional
    public ResponseEntity<String> createPedido(pedidorequest request) {
        pedido nuevo = new pedido();
        List<pedidoproducto> listpedidoproducto = new ArrayList<>();

        nuevo.setNombre(request.getNombre());
        nuevo.setApellido(request.getApellido());
        nuevo.setDireccion(request.getDireccion());
        nuevo.setTelefono(request.getTelefono());
        nuevo.setEmail(request.getEmail());
        nuevo.setFecha(LocalDateTime.now(ZoneId.of("America/Lima")));
        nuevo.setFechaderecojo(request.getFechaderecojo());
        nuevo.setFechapago(request.getFechapago());
        nuevo.setEstado(request.getEstado());
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            usuario usuario = usuariorepository.findByEmail((String) auth.getPrincipal());
            nuevo.setUsuario(usuario);
        }

        for (pedidoproductorequest p : request.getProductos()) {
            pedidoproducto pedidoProducto = new pedidoproducto();
            ProductoSucursal productoSucursal = productosucursalrepository.findById(p.getIdProductoSucursal())
                    .orElseThrow(() -> new RuntimeException(
                            "No se encontró el producto por sucursal con ID: " + p.getIdProductoSucursal()));
            pedidoProducto.setProductoSucursal(productoSucursal);

            pedidoProducto.setCantidad(p.getCantidad());
            pedidoProducto.setSubtotal(productoSucursal.getProducto().getPrecio() * p.getCantidad());
            pedidoProducto.setPedido(nuevo);
            pedidoProducto.setPedido(nuevo);
            listpedidoproducto.add(pedidoProducto);
        }
        nuevo.setPedidoProducto(listpedidoproducto);
        pedidorepository.save(nuevo);
        return ResponseEntity.ok("Pedido creado correctamente");
    }

    @Override
    @Transactional
    public ResponseEntity<String> updatePedido(int idPedido, pedidoupdaterequest pedido) {
        pedido pedidoexistente = pedidorepository.findById(idPedido)
                .orElseThrow(() -> new RuntimeException("No se encontró el pedido con ID: " + idPedido));

        if (pedido.getEstado() != "PAGADO") {
            if (pedido.getNombre() != null) {
                pedidoexistente.setNombre(pedido.getNombre());
            }
            if (pedido.getApellido() != null) {
                pedidoexistente.setApellido(pedido.getApellido());
            }
            if (pedido.getDireccion() != null) {
                pedidoexistente.setDireccion(pedido.getDireccion());
            }
            if (pedido.getTelefono() != null) {
                pedidoexistente.setTelefono(pedido.getTelefono());
            }
            if (pedido.getEmail() != null) {
                pedidoexistente.setEmail(pedido.getEmail());
            }
            if (pedido.getEstado() != null) {
                pedidoexistente.setEstado(pedido.getEstado());
            }
        }

        try {
            pedidorepository.save(pedidoexistente);
            return ResponseEntity.ok("Pedido actualizado correctamente");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al actualizar el pedido: " + e.getMessage());

        }

    }

    @Override
    @Transactional
    public ResponseEntity<String> deletePedido(int idPedido) {
        try {
            pedidorepository.deleteById(idPedido);
            return ResponseEntity.ok("Pedido eliminado correctamente");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al eliminar el pedido: " + e.getMessage());
        }
    }

    @Override
    public pedido pedidobyusuario(String email) {
        return pedidorepository.findAll().stream()
                .filter((pedido) -> pedido.getEmail().equals(email) || pedido.getUsuario().getEmail().equals(email))
                .findFirst().orElseThrow(() -> new RuntimeException("No se encontró el pedido con email: " + email));
    }

}
