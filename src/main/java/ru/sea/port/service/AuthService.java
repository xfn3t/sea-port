package ru.sea.port.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import ru.sea.port.dto.LoginRequest;
import ru.sea.port.dto.LoginResponse;
import ru.sea.port.dto.RegisterRequest;
import ru.sea.port.dto.RoleDto;
import ru.sea.port.dto.UserDto;
import ru.sea.port.entity.CustomUserDetails;
import ru.sea.port.entity.Role;
import ru.sea.port.entity.User;
import ru.sea.port.exception.ResourceNotFoundException;
import ru.sea.port.mapper.UserMapper;
import ru.sea.port.repository.RoleRepository;
import ru.sea.port.repository.UserRepository;
import ru.sea.port.security.JwtTokenProvider;

/**
 * Сервис аутентификации и регистрации пользователей.
 * Инжектирует AuthenticationManager и JwtTokenProvider для логина,
 * а также репозитории и мапперы для регистрации.
 */
@Service
@AllArgsConstructor
public class AuthService {

    private final AuthenticationManager authManager;
    private final JwtTokenProvider tokenProvider;
    private final UserRepository userRepo;
    private final RoleRepository roleRepo;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;  // вот это поле

    /**
     * Выполняет аутентификацию пользователя через AuthenticationManager
     * и возвращает JWT и роль в LoginResponse.
     *
     * @param req DTO с полями email и password
     * @return LoginResponse с токеном и RoleDto
     */
    public LoginResponse login(LoginRequest req) {
        Authentication auth = authManager.authenticate(
            new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword())
        );
        String jwt = tokenProvider.createToken(auth);
        User principal = ((CustomUserDetails) auth.getPrincipal()).getUser();
        RoleDto roleDto = new RoleDto(
            principal.getRole().getId(),
            principal.getRole().getRole()
        );
        return new LoginResponse(jwt, roleDto);
    }


    /**
     * Логика регистрации нового пользователя.
     * Кодирует пароль через PasswordEncoder и сохраняет вместе с ролью.
     *
     * @param req DTO с данными нового пользователя и roleId
     * @return UserDto сохранённого пользователя
     * @throws IllegalArgumentException при занятом email
     * @throws ResourceNotFoundException если роль не найдена
     */

    public UserDto register(RegisterRequest req) {
        if (userRepo.findByEmail(req.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email уже занят");
        }

        var role = roleRepo.findById(req.getRoleId())
            .orElseThrow(() -> new ResourceNotFoundException("Role", "id", req.getRoleId()));

        User user = User.builder()
            .firstName(req.getFirstName())
            .patronomic(req.getPatronomic())
            .lastName(req.getLastName())
            .email(req.getEmail())
            .password(passwordEncoder.encode(req.getPassword()))
            .role(role)
            .build();

   
        User saved = userRepo.save(user);
        return userMapper.toDto(saved);
    }


}

