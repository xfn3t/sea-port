package ru.sea.port.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO для передачи информации о роли.
 * <p>
 * Используется в ответах API (LoginResponse, UserDto и т.п.), чтобы клиент
 * видел только ID и имя роли, без лишних полей сущности.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RoleDto {

    private Long id;

    /**
     * Текстовое имя роли, например: ADMIN, TERMINAL_OPERATOR, TALLYMAN.
     */
    private String role;
}
