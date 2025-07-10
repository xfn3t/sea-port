
package ru.sea.port.mapper;

import org.mapstruct.Mapper;

import ru.sea.port.dto.RoleDto;
import ru.sea.port.entity.Role;

/**
 * Mapper для преобразования Role ↔ RoleDto.
 * Аннотация componentModel="spring" генерирует бины в контексте Spring.
 *
 */
@Mapper(componentModel = "spring")
public interface RoleMapper {

    RoleDto toDto(Role entity);
    Role toEntity(RoleDto dto);

}