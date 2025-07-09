package ru.sea.port.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO для входа в систему (login).
 * <p>
 * Используется в контроллере {@code AuthController.login} для десериализации JSON из тела запроса:
 * <pre>{@code
 * {
 *   "email": "user@example.com",
 *   "password": "secret"
 * }
 * }</pre>
 * <p>
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class LoginRequest {

    /**
     * Электронная почта пользователя.
     * Служит идентификатором при аутентификации.
     * Формат: валидный email.
     */
    private String email;

    /**
     * Пароль пользователя.
     * Сравнивается с хешем, хранимым в базе, через {@link org.springframework.security.crypto.password.PasswordEncoder}.
     */
    private String password;

}
