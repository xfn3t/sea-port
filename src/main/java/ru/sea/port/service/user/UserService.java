package ru.sea.port.service.user;

import java.util.List;

import ru.sea.port.dto.UserDto;

/**
 * Интерфейс сервиса управления пользователями (CRUD).
 */
public interface UserService {
    List<UserDto> getAllUsers();
    UserDto getUserById(Long id);
    UserDto createUser(UserDto dto);
    UserDto updateUser(Long id, UserDto dto);
    void deleteUser(Long id);
}

