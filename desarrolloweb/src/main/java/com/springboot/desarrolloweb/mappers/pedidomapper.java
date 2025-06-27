package com.springboot.desarrolloweb.mappers;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.springboot.desarrolloweb.DTO.pedidopersonaldto;
import com.springboot.desarrolloweb.entity.pedido;
import com.springboot.desarrolloweb.entity.pedidoproducto;

@Mapper(componentModel = "spring")
public interface pedidomapper {

        @Mapping(target = "idpedido", source = "pedido.idpedido")
        @Mapping(target = "nombre", source = "pedido.nombre")
        @Mapping(target = "apellido", source = "pedido.apellido")
        @Mapping(target = "email", source = "pedido.email")
        @Mapping(target = "direccion", source = "pedido.direccion")
        @Mapping(target = "fechaderecojo", source = "pedido.fechaderecojo")
        @Mapping(target = "fechapedido", source = "pedido.fecha")
        @Mapping(target = "fechapago", source = "pedido.fechapago")
        @Mapping(target = "estado", source = "pedido.estado")
        @Mapping(target = "pedidoProducto", source = "pedido.pedidoProducto")
        @Mapping(target = "usuario", source = "pedido.usuario")
        pedidopersonaldto pedidotopedidopersonaldto(pedido pedido);

        // Mapeo para PedidoProductoDTO
        @Mapping(target = "pedidoproductoid", source = "pedidoproductoid")
        @Mapping(target = "cantidad", source = "cantidad")
        @Mapping(target = "subtotal", source = "subtotal")
        @Mapping(target = "productoSucursal", source = "productoSucursal")
        pedidopersonaldto.PedidoProductoDTO pedidoproductotopedidoproductodto(pedidoproducto pedidoproducto);

        // Mapeo para ProductoSucursalDTO
        @Mapping(target = "idProductoSucursal", source = "idProductoSucursal")
        @Mapping(target = "stock", source = "stock")
        @Mapping(target = "activo", source = "activo")
        @Mapping(target = "estado", source = "estado")
        @Mapping(target = "producto", source = "producto")
        @Mapping(target = "sucursal", source = "sucursal")
        pedidopersonaldto.ProductoSucursalDTO productosucursaltoproductosucursaldto(
                        com.springboot.desarrolloweb.entity.ProductoSucursal productoSucursal);

        // Mapeo para ProductoInfoDTO
        @Mapping(target = "idproducto", source = "idproducto")
        @Mapping(target = "nombre", source = "nombre")
        @Mapping(target = "descripcion", source = "descripcion")
        @Mapping(target = "precio", source = "precio")
        @Mapping(target = "imagen", source = "imagen")
        @Mapping(target = "estado", source = "estado")
        @Mapping(target = "categoria", source = "categoria")
        pedidopersonaldto.ProductoInfoDTO productotoproductoinfodto(
                        com.springboot.desarrolloweb.entity.producto producto);

        // Mapeo para SucursalInfoDTO
        @Mapping(target = "idsucursal", source = "idsucursal")
        @Mapping(target = "nombre", source = "nombre")
        @Mapping(target = "direccion", source = "direccion")
        @Mapping(target = "ciudad", source = "ciudad")
        @Mapping(target = "codigoPropio", source = "codigoPropio")
        @Mapping(target = "lat", source = "lat")
        @Mapping(target = "lon", source = "lon")
        pedidopersonaldto.SucursalInfoDTO sucursaltosucursalinfodto(
                        com.springboot.desarrolloweb.entity.sucursal sucursal);

        // Mapeo para CategoriaInfoDTO
        @Mapping(target = "id", source = "id")
        @Mapping(target = "name", source = "name")
        pedidopersonaldto.CategoriaInfoDTO categoriatocategoriainfodto(
                        com.springboot.desarrolloweb.entity.categoria categoria);

        // Mapeo para UsuarioDTO
        // @Mapping(target = "idusuario", source = "idusuario")
        // @Mapping(target = "nombre", source = "nombre")
        // @Mapping(target = "apellido", source = "apellido")
        @Mapping(target = "email", source = "email")
        // @Mapping(target = "telefono", source = "telefono")
        pedidopersonaldto.UsuarioDTO usuariotousuariodto(com.springboot.desarrolloweb.entity.usuario usuario);

        List<pedidopersonaldto> pedidotopedidopersonaldtoList(List<pedido> pedidos);

}
