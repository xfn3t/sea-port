package ru.sea.port.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * DTO для регистрации нового пользователя.
 * <p>
 * Используется в контроллере {@code AuthController.register} для десериализации JSON:
 * <pre>{@code
 * {
 *   "firstName": "Ivan",
 *   "patronomic": "Ivanovich",
 *   "lastName": "Ivanov",
 *   "email": "ivan@local.test",
 *   "password": "password123",
 *   "roleId": 1
 * }
 * }</pre>
 * <p>
 * Преобразуется сервисом {@code AuthService.register} в сущность {@code User} с шифрованием пароля.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class RegisterRequest {
    private String firstName;
    private String patronomic;
    private String lastName;
    private String email;
    private String password;
    private Long roleId; // чтобы админ мог сразу назначить роль
}

