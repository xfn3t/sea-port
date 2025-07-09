package ru.sea.port.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


import lombok.RequiredArgsConstructor;
import ru.sea.port.dto.*;
import ru.sea.port.service.AuthService;

/**
 * REST-контроллер для аутентификации и регистрации пользователей.
 * <p>
 * Эндпоинты:
 * <ul>
 *   <li><b>POST /api/auth/login</b>  — логин с выдачей JWT.</li>
 *   <li><b>POST /api/auth/register</b> — регистрация (по умолчанию закрыта через denyAll).</li>
 * </ul>
 *
 * @see org.springframework.web.bind.annotation.RestController
 * @see org.springframework.web.bind.annotation.RequestMapping
 * @see org.springframework.security.access.prepost.PreAuthorize
 * 
 * Аннотации:
 * 
 * @RestController  — контроллер, где каждый метод по умолчанию помечен @ResponseBody 
 * @ResponseBody    — указывает Spring сериализовать возвращаемое методом значение (например, в JSON) и поместить его прямо в тело HTTP‑ответа
 * @Controller      — контроллер MVC (возвращает view).
 * @RequestMapping  — общий URL-путь для всех методов класса.
 * @GetMapping      — отвечает на HTTP GET.
 * @PostMapping     — отвечает на HTTP POST.
 * @PutMapping      — отвечает на HTTP PUT.
 * @DeleteMapping   — отвечает на HTTP DELETE.
 * @PreAuthorize    — SpEL-правило доступа до выполнения метода.
 * @RequiredArgsConstructor — Lombok: конструктор для всех final-полей.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * Аутентифицирует пользователя и возвращает JWT + роль.
     *
     * @param req объект с полями email и password.
     * @return HTTP 200 и тело {@link LoginResponse} при успешном логине.
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest req) {
        return ResponseEntity.ok(authService.login(req));
    }

    /**
     * Регистрирует нового пользователя с указанными данными.
     * <p>Доступ закрыт через {@code @PreAuthorize("denyAll()")} — включать для создания начальных 
     * пользователей или необходимости наличия у пользователей регистрации.</p>
     *
     * @param req объект с регистрационными данными {@link RegisterRequest}.
     * @return HTTP 200 и DTO созданного пользователя {@link UserDto}.
     */
    @PostMapping("/register")
    @PreAuthorize("denyAll()")
    public ResponseEntity<UserDto> register(@RequestBody RegisterRequest req) {
        return ResponseEntity.ok(authService.register(req));
    }

}
