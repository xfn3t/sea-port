package ru.sea.port.service.user;


import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import ru.sea.port.dto.UserDto;
import ru.sea.port.model.user.User;
import ru.sea.port.mapper.UserMapper;
import ru.sea.port.repository.user.RoleRepository;
import ru.sea.port.repository.user.UserRepository;


/**
 * Реализация UserService для CRUD над User.
 * Кодирует пароль при создании/обновлении и валидает существование ролей.
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
            .map(userMapper::toDto)
            .collect(Collectors.toList());
    }

    @Override
    public UserDto getUserById(Long id) {
        return userRepository.findById(id)
            .map(userMapper::toDto)
            .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }

    /**
     * Создаёт нового пользователя. Кодирует пароль и назначает роль по dto.getRole().getId().
     *
     * @param dto входной UserDto
     * @return UserDto созданного пользователя
     */
    @Override
    public UserDto createUser(UserDto dto) {
        User user = userMapper.toEntity(dto);
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole(roleRepository.findById(dto.getRole().getId())
            .orElseThrow(() -> new EntityNotFoundException("Role not found")));
        return userMapper.toDto(userRepository.save(user));
    }

    /**
     * Обновляет существующего пользователя: поля, пароль (если не пустой) и роль.
     *
     * @param id  идентификатор пользователя
     * @param dto DTO с новыми значениями
     * @return обновлённый UserDto
     */
    @Override
    public UserDto updateUser(Long id, UserDto dto) {
        User existing = userRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("User not found"));

        existing.setFirstName(dto.getFirstName());
        existing.setLastName(dto.getLastName());
        existing.setPatronomic(dto.getPatronomic());
        existing.setEmail(dto.getEmail());
        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            existing.setPassword(passwordEncoder.encode(dto.getPassword()));
        }
        if (dto.getRole() != null) {
            existing.setRole(roleRepository.findById(dto.getRole().getId())
                .orElseThrow(() -> new EntityNotFoundException("Role not found")));
        }

        return userMapper.toDto(userRepository.save(existing));
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}
