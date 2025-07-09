package ru.sea.port.dto;


import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * DTO для передачи данных пользователя клиенту и между слоями приложения.
 * <p>
 * Применяется в CRUD-эндпоинтах {@code UserController}:
 * <ul>
 *   <li>При создании/обновлении {@code createUser/updateUser}</li>
 *   <li>При отдаче списка и конкретного пользователя {@code getAllUsers/getUser}</li>
 * </ul>
 * <p>
 * Содержит основные поля, а также пароль (только при создании/обновлении).
 * Пароль в ответах контроллера опускается (аннотация {@code @JsonIgnore}).
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UserDto {
    
    private Long id;
    private String firstName;
    private String patronomic;
    private String lastName;
    private String email;
    private RoleDto role;

    //@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

}
