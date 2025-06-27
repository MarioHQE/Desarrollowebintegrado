package com.springboot.desarrolloweb.DTO;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

@Data
public class pedidopersonaldto {
    private int idpedido;
    private String nombre;
    private String apellido;
    private String email;
    private String telefono;
    private String direccion;
    private LocalDateTime fechaderecojo;
    private LocalDateTime fechapedido;
    private LocalDateTime fechapago;
    private String estado;
    private List<PedidoProductoDTO> pedidoProducto; // Cambio aquí
    private UsuarioDTO usuario;

    @Data
    public static class PedidoProductoDTO {
        private int pedidoproductoid;
        private int cantidad;
        private double subtotal;

        // Información completa del ProductoSucursal
        private ProductoSucursalDTO productoSucursal;
    }

    @Data
    public static class ProductoSucursalDTO {
        private int idProductoSucursal;
        private int stock;
        private boolean activo;
        private String estado;

        // Información del producto
        private ProductoInfoDTO producto;

        // Información de la sucursal
        private SucursalInfoDTO sucursal;
    }

    @Data
    public static class ProductoInfoDTO {
        private int idproducto;
        private String nombre;
        private String descripcion;
        private double precio;
        private String imagen;
        private boolean estado;

        // Información de la categoría
        private CategoriaInfoDTO categoria;
    }

    @Data
    public static class SucursalInfoDTO {
        private int idsucursal;
        private String nombre;
        private String direccion;
        private String ciudad;
        private String codigoPropio;
        private double lat;
        private double lon;
    }

    @Data
    public static class CategoriaInfoDTO {
        private int id;
        private String name;
    }

    @Data
    public static class UsuarioDTO {

        private String email;

    }
}