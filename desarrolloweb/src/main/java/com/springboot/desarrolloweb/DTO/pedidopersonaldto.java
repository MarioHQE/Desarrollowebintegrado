package com.springboot.desarrolloweb.DTO;

import java.time.LocalDateTime;
import java.util.List;

import com.springboot.desarrolloweb.entity.pedidoproducto;

import lombok.Data;

@Data
public class pedidopersonaldto {
    int idpedido;
    String nombre;
    String apellido;
    String email;
    String direccion;
    LocalDateTime fechaderecojo;
    LocalDateTime fecha;
    LocalDateTime fechapago;
    boolean estado;
    List<pedidoproducto> pedidoProducto;
    private UsuarioDTO usuario;

    @Data
    public static class ProductoInfoDTO {
        private int idproducto;
        private String nombre;
        private String descripcion;
        private double precio;
        private String imagen;
        private List<PedidoProductoDTO> productos;
    }

    @Data
    public static class PedidoProductoDTO {
        private int pedidoproductoid;
        private int cantidad;
        private double subtotal;

        // Información del producto
        private ProductoInfoDTO producto;

        // Información de la sucursal
        private SucursalInfoDTO sucursal;
    }

    // DTO para información básica de la sucursal
    @Data
    public static class SucursalInfoDTO {
        private int idsucursal;
        private String nombre;
        private String direccion;
        private String ciudad;
    }

    @Data
    public static class UsuarioDTO {

        private String email;
    }
}