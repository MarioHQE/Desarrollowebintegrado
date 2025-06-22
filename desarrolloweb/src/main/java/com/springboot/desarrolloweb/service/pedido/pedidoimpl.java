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
import com.springboot.desarrolloweb.DTO.pedidopersonaldto;
import com.springboot.desarrolloweb.dao.pedidorepository;
import com.springboot.desarrolloweb.dao.productosucursalrepository;
import com.springboot.desarrolloweb.dao.usuariorepository;
import com.springboot.desarrolloweb.entity.ProductoSucursal;
import com.springboot.desarrolloweb.entity.pedido;
import com.springboot.desarrolloweb.entity.pedidoproducto;
import com.springboot.desarrolloweb.entity.usuario;
import com.springboot.desarrolloweb.mappers.pedidomapper;
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
    @Autowired
    pedidomapper mapper;
    // Estados del pedido
    private static final String ESTADO_PENDIENTE = "PENDIENTE";
    private static final String ESTADO_PAGADO = "PAGADO";
    private static final String ESTADO_PREPARANDO = "PREPARANDO";
    private static final String ESTADO_LISTO = "LISTO_PARA_RECOGER";
    private static final String ESTADO_ENTREGADO = "ENTREGADO";
    private static final String ESTADO_CANCELADO = "CANCELADO";

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
        nuevo.setEstado(ESTADO_PENDIENTE);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            log.info("El usuario autenticado es: " + auth.getName());
            usuario usuario = usuariorepository.findByEmail(auth.getName()).orElseThrow(
                    () -> new RuntimeException("No se encontró el usuario"));
            nuevo.setUsuario(usuario);
        }

        // Verificar stock y crear reservas
        for (pedidoproductorequest p : request.getProductos()) {
            pedidoproducto pedidoProducto = new pedidoproducto();
            ProductoSucursal productoSucursal = productosucursalrepository.findById(p.getIdProductoSucursal())
                    .orElseThrow(() -> new RuntimeException(
                            "No se encontró el producto por sucursal con ID: " + p.getIdProductoSucursal()));

            // Calcular stock disponible (stock total - stock reservado)
            int stockDisponible = productoSucursal.getStock() - productoSucursal.getStockReservado();

            if (stockDisponible < p.getCantidad()) {
                return ResponseEntity.badRequest()
                        .body("No hay suficiente stock para el producto " + productoSucursal.getProducto().getNombre()
                                + ". Stock disponible: " + stockDisponible);
            }

            if (productoSucursal.getProducto().isEstado() == false) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("El producto " + productoSucursal.getProducto().getNombre() + " no esta disponible");
            }

            // SOLO reservar el stock, mantenerlo reservado hasta la entrega
            productosucursalrepository.save(productoSucursal);

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

        String estadoAnterior = pedidoexistente.getEstado();

        // REGLA 1: No permitir cambios en pedidos ENTREGADOS
        if (ESTADO_ENTREGADO.equals(pedidoexistente.getEstado())) {
            log.warn("Intento de modificar pedido entregado: " + idPedido);
            return ResponseEntity.badRequest()
                    .body("No se puede modificar un pedido que ya ha sido entregado");
        }

        // REGLA 2: No permitir cambiar manualmente a estado PAGADO (solo vía webhook)
        if (pedido.getEstado() != null && ESTADO_PAGADO.equals(pedido.getEstado())) {
            log.warn("Intento de cambiar manualmente estado a PAGADO para pedido: " + idPedido);
            return ResponseEntity.badRequest()
                    .body("El estado PAGADO solo puede ser establecido mediante el proceso de pago");
        }

        // REGLA 3: Solo permitir cambios de datos básicos en pedidos PAGADOS
        if (ESTADO_PAGADO.equals(pedidoexistente.getEstado()) ||
                ESTADO_PREPARANDO.equals(pedidoexistente.getEstado()) ||
                ESTADO_LISTO.equals(pedidoexistente.getEstado())) {
            // Solo permitir cambios muy limitados
            if (pedido.getTelefono() != null) {
                pedidoexistente.setTelefono(pedido.getTelefono());
            }
            if (pedido.getEmail() != null) {
                pedidoexistente.setEmail(pedido.getEmail());
            }

            // Permitir cambios de estado en la secuencia correcta
            if (pedido.getEstado() != null) {
                if (validarTransicionEstado(estadoAnterior, pedido.getEstado())) {
                    pedidoexistente.setEstado(pedido.getEstado());

                    // Si se marca como ENTREGADO, liberar el stock
                    if (ESTADO_ENTREGADO.equals(pedido.getEstado())) {
                        procesarEntrega(pedidoexistente);
                    }
                } else {
                    return ResponseEntity.badRequest()
                            .body("Transición de estado no válida: " + estadoAnterior + " -> " + pedido.getEstado());
                }
            }
        } else {
            // Para pedidos PENDIENTES, permitir más cambios
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

            // Manejar cancelación
            if (pedido.getEstado() != null && ESTADO_CANCELADO.equals(pedido.getEstado())) {
                if (ESTADO_PENDIENTE.equals(estadoAnterior)) {
                    log.info("Procesando cancelación de pedido: " + idPedido);
                    liberarStockReservado(pedidoexistente);
                    pedidoexistente.setEstado(ESTADO_CANCELADO);
                } else {
                    return ResponseEntity.badRequest()
                            .body("Solo se pueden cancelar pedidos en estado PENDIENTE");
                }
            }
        }

        try {
            pedidorepository.save(pedidoexistente);
            return ResponseEntity.ok().body("Pedido actualizado correctamente");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al actualizar el pedido: " + e.getMessage());
        }
    }

    private boolean validarTransicionEstado(String estadoActual, String estadoNuevo) {
        // Definir las transiciones válidas
        if (ESTADO_PAGADO.equals(estadoActual) && ESTADO_PREPARANDO.equals(estadoNuevo))
            return true;
        if (ESTADO_PREPARANDO.equals(estadoActual) && ESTADO_LISTO.equals(estadoNuevo))
            return true;
        if (ESTADO_LISTO.equals(estadoActual) && ESTADO_ENTREGADO.equals(estadoNuevo))
            return true;

        // Permitir cancelar desde PAGADO (con reembolso)
        if (ESTADO_PAGADO.equals(estadoActual) && ESTADO_CANCELADO.equals(estadoNuevo))
            return true;

        return false;
    }

    private void procesarEntrega(pedido pedido) {
        log.info("Procesando entrega del pedido: " + pedido.getIdpedido());

        for (pedidoproducto pp : pedido.getPedidoProducto()) {
            ProductoSucursal productoSucursal = pp.getProductoSucursal();

            // Ahora sí reducir el stock definitivo
            productoSucursal.setStock(productoSucursal.getStock() - pp.getCantidad());

            // Liberar el stock reservado
            productoSucursal.setStockReservado(productoSucursal.getStockReservado() - pp.getCantidad());

            log.info("Entregado - Producto: " + productoSucursal.getProducto().getNombre() +
                    " - Stock actual: " + productoSucursal.getStock() +
                    " - Stock reservado: " + productoSucursal.getStockReservado());

            productosucursalrepository.save(productoSucursal);
        }
        // Marcar que el stock fue procesado
        pedido.setStockProcesado(true);
    }

    private void liberarStockReservado(pedido pedido) {
        log.info("Liberando stock reservado para pedido cancelado: " + pedido.getIdpedido());

        for (pedidoproducto pp : pedido.getPedidoProducto()) {
            ProductoSucursal productoSucursal = pp.getProductoSucursal();

            // Liberar el stock reservado sin afectar el stock real
            productoSucursal.setStockReservado(productoSucursal.getStockReservado() - pp.getCantidad());

            log.info("Liberando " + pp.getCantidad() + " unidades del producto: " +
                    productoSucursal.getProducto().getNombre());

            productosucursalrepository.save(productoSucursal);
        }
    }

    @Override
    @Transactional
    public ResponseEntity<String> deletePedido(int idPedido) {
        try {
            pedido pedidoexistente = pedidorepository.findById(idPedido)
                    .orElseThrow(() -> new RuntimeException("No se encontró el pedido con ID: " + idPedido));

            // Solo permitir eliminar pedidos PENDIENTES o CANCELADOS
            if (!ESTADO_PENDIENTE.equals(pedidoexistente.getEstado()) &&
                    !ESTADO_CANCELADO.equals(pedidoexistente.getEstado())) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("Solo se pueden eliminar pedidos en estado PENDIENTE o CANCELADO");
            }

            // Liberar stock reservado si está pendiente
            if (ESTADO_PENDIENTE.equals(pedidoexistente.getEstado())) {
                liberarStockReservado(pedidoexistente);
            }

            pedidorepository.delete(pedidoexistente);
            return ResponseEntity.ok("Pedido eliminado correctamente");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al eliminar el pedido: " + e.getMessage());
        }
    }

    @Override
    public List<pedidopersonaldto> pedidobyusuario(String email) {

        return mapper.pedidotopedidopersonaldtoList(pedidorepository.findbyusuario(email));

    }

    // Método para procesar el pago desde el webhook de Stripe
    @Transactional
    public ResponseEntity<String> procesarPagoExitoso(int idPedido) {
        try {
            pedido pedidoExistente = pedidorepository.findById(idPedido)
                    .orElseThrow(() -> new RuntimeException("No se encontró el pedido con ID: " + idPedido));

            // Verificar que el pedido esté en estado PENDIENTE
            if (!ESTADO_PENDIENTE.equals(pedidoExistente.getEstado())) {
                log.error("El pedido " + idPedido + " no está en estado PENDIENTE. Estado actual: " +
                        pedidoExistente.getEstado());
                return ResponseEntity.badRequest()
                        .body("El pedido no puede ser procesado. Estado actual: " + pedidoExistente.getEstado());
            }

            // Actualizar estado a PAGADO
            pedidoExistente.getPedidoProducto().forEach(t -> {
                ProductoSucursal productoSucursal = productosucursalrepository
                        .findById(t.getProductoSucursal().getIdProductoSucursal()).orElseThrow(
                                () -> new RuntimeException("No se encontró el producto por sucursal con ID: "
                                        + t.getProductoSucursal().getIdProductoSucursal()));
                productoSucursal.setStockReservado(productoSucursal.getStockReservado() + t.getCantidad());

            });
            pedidoExistente.setEstado(ESTADO_PAGADO);
            pedidoExistente.setFechapago(LocalDateTime.now(ZoneId.of("America/Lima")));

            // NO procesar el stock aquí, solo mantenerlo reservado
            // El stock se procesará cuando se marque como ENTREGADO

            pedidorepository.save(pedidoExistente);

            log.info("Pago procesado exitosamente para pedido: " + idPedido +
                    ". Stock permanece reservado hasta la entrega.");
            return ResponseEntity.ok("Pago procesado exitosamente");
        } catch (Exception e) {
            log.error("Error al procesar pago exitoso para pedido " + idPedido, e);
            return ResponseEntity.badRequest().body("Error al procesar el pago: " + e.getMessage());
        }
    }

    // Método adicional para marcar pedido como entregado
    @Transactional
    public ResponseEntity<String> marcarComoEntregado(int idPedido) {
        try {
            pedidoupdaterequest updateRequest = new pedidoupdaterequest();
            updateRequest.setEstado(ESTADO_ENTREGADO);
            return updatePedido(idPedido, updateRequest);
        } catch (Exception e) {
            log.error("Error al marcar pedido como entregado: " + idPedido, e);
            return ResponseEntity.badRequest().body("Error al marcar como entregado: " + e.getMessage());
        }
    }

}