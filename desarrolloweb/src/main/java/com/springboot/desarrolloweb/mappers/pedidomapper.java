package com.springboot.desarrolloweb.mappers;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.springboot.desarrolloweb.DTO.pedidopersonaldto;
import com.springboot.desarrolloweb.entity.pedido;

@Mapper(componentModel = "spring")
public interface pedidomapper {
    @Mapping(target = "idpedido", source = "pedido.idpedido")
    @Mapping(target = "nombre", source = "pedido.nombre")
    @Mapping(target = "apellido", source = "pedido.apellido")
    @Mapping(target = "email", source = "pedido.email")
    @Mapping(target = "direccion", source = "pedido.direccion")
    @Mapping(target = "fechaderecojo", source = "pedido.fechaderecojo")
    @Mapping(target = "fecha", source = "pedido.fecha")
    @Mapping(target = "fechapago", source = "pedido.fechapago")
    @Mapping(target = "estado", source = "pedido.estado")
    @Mapping(target = "pedidoProducto", source = "pedido.pedidoProducto")
    @Mapping(target = "usuario", source = "pedido.usuario")
    pedidopersonaldto pedidotopedidopersonaldto(pedido pedido);

    List<pedidopersonaldto> pedidotopedidopersonaldtoList(List<pedido> pedido);

}
