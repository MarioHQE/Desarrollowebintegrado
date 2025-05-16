package com.springboot.desarrolloweb.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.springboot.desarrolloweb.DTO.productoDTO;
import com.springboot.desarrolloweb.entity.producto;

@Mapper(componentModel = "spring")
public interface productomapper {

    @Mapping(target = "nombre", source = "producto.nombre")
    @Mapping(target = "descripcion", source = "producto.descripcion")
    @Mapping(target = "precio", source = "producto.precio")
    public productoDTO productotoproductodtop(producto producto);

}
