package ru.sea.port.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * DTO-ответ при успешной аутентификации.
 * <p>
 * Возвращается из контроллера {@code AuthController.login} в формате:
 * <pre>{@code
 * {
 *   "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
 *   "role": { "id": 1, "role": "ADMIN" }
 * }
 * }</pre>
 * <p>
 * Содержит сгенерированный JWT и минимальную информацию о роли пользователя.
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class LoginResponse {

    /**
     * JWT-токен, который клиент должен передавать в заголовке Authorization:
     * <c>Authorization: Bearer &lt;token&gt;</c>.
     */
    private String token;
    
    /**
     * Роль пользователя, включающая идентификатор и имя роли.
     * {@link RoleDto#id} и {@link RoleDto#role}.
     */
    private RoleDto role;

}
