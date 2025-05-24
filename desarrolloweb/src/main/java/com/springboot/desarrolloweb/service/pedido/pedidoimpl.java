package com.springboot.desarrolloweb.service.pedido;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
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
    public ResponseEntity<String> createPedido(pedidorequest request) throws JsonProcessingException {
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
        nuevo.setEstado("PENDIENTE");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            log.info("El usuario autenticado es: " + auth.getName());
            usuario usuario = usuariorepository.findByEmail(auth.getName()).orElseThrow(
                    () -> new RuntimeException("No se encontró el usuario"));
            nuevo.setUsuario(usuario);
        }

        for (pedidoproductorequest p : request.getProductos()) {
            pedidoproducto pedidoProducto = new pedidoproducto();
            ProductoSucursal productoSucursal = productosucursalrepository.findById(p.getIdProductoSucursal())
                    .orElseThrow(() -> new RuntimeException(
                            "No se encontró el producto por sucursal con ID: " + p.getIdProductoSucursal()));
            if (productoSucursal.getStock() <= p.getCantidad()) {
                return ResponseEntity.badRequest()
                        .body("No hay suficiente stock para el producto " + productoSucursal.getProducto().getNombre());

            }
            if (productoSucursal.getProducto().isEstado() == false) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("El producto " + productoSucursal.getProducto().getNombre() + " esta inactivo");

            }

            pedidoProducto.setProductoSucursal(productoSucursal);

            pedidoProducto.setCantidad(p.getCantidad());
            pedidoProducto.setSubtotal(productoSucursal.getProducto().getPrecio() * p.getCantidad());
            pedidoProducto.setPedido(nuevo);
            listpedidoproducto.add(pedidoProducto);
        }
        nuevo.setPedidoProducto(listpedidoproducto);
        ObjectMapper mapper = new ObjectMapper();
        HashMap<String, Object> map = new HashMap<>();

        pedido pedidoguardado = pedidorepository.save(nuevo);
        map.put("idpedido", pedidoguardado.getIdpedido());
        String json = mapper.writeValueAsString(map);
        return ResponseEntity.ok(json);
    }

    @Override
    @Transactional
    public ResponseEntity<String> updatePedido(int idPedido, pedidoupdaterequest pedido) {
        pedido pedidoexistente = pedidorepository.findById(idPedido)
                .orElseThrow(() -> new RuntimeException("No se encontró el pedido con ID: " + idPedido));

        if (!"PAGADO".equals(pedidoexistente.getEstado())) {
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
            pedido pedidoexistente = pedidorepository.findById(idPedido)
                    .orElseThrow(() -> new RuntimeException("No se encontró el pedido con ID: " + idPedido));
            if (pedidoexistente.getEstado().equals("PAGADO")) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("No se puede eliminar un pedido pagado");
            }
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
