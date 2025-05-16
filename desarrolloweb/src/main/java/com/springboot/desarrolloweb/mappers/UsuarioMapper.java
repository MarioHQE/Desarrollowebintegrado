package com.springboot.desarrolloweb.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import com.springboot.desarrolloweb.DTO.userDTO;
import com.springboot.desarrolloweb.entity.usuario;

@Mapper(componentModel = "spring")
public interface UsuarioMapper {

    @Mapping(target = "nombre", source = "user.nombre")
    @Mapping(target = "apellido", source = "user.apellido")
    @Mapping(target = "email", source = "user.email")
    @Mapping(target = "telefono", source = "user.telefono")
    userDTO usertoDTO(usuario user);

}
