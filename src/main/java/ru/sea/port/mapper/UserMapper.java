package ru.sea.port.mapper;

import org.mapstruct.Mapper;

import ru.sea.port.dto.UserDto;
import ru.sea.port.model.user.User;

/**
 * Mapper для преобразования User ↔ UserDto.
 * Параметр uses = RoleMapper указывает на зависимость для маппинга ролей.
 *
 */
@Mapper(componentModel = "spring", uses = RoleMapper.class)
public interface UserMapper {

    UserDto toDto(User entity);
    User   toEntity(UserDto dto);
}