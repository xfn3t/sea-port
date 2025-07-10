package ru.sea.port.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import ru.sea.port.model.user.Role;
import ru.sea.port.repository.user.RoleRepository;
import ru.sea.port.security.JwtAuthenticationFilter;

/**
 * Основная конфигурация Spring Security для приложения.
 * 
 * Аннотации:
 *  - @Configuration: помечает класс как источник бинов.
 *  - @EnableWebSecurity: включает веб‑безопасность Spring Security.
 *  - @EnableMethodSecurity(prePostEnabled = true): позволяет использовать
 *    аннотации @PreAuthorize/@PostAuthorize над методами.
 * 
 * Внутри определены бины:
 *  - SecurityFilterChain: настраивает HttpSecurity (CSRF, сессии, разрешения URL).
 *  - AuthenticationManager: поставщик аутентификации через DaoAuthenticationProvider.
 *  - PasswordEncoder: BCrypt с итерациями 12.
 *  - CommandLineRunner: при старте создаёт дефолтные роли в БД.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    /**
     * Настраивает цепочку фильтров безопасности:
     *  - Отключает CSRF (т.к. используем JWT).
     *  - Устанавливает Stateless‑сессии.
     *  - Разрешает анонимный доступ к /api/auth/**, все остальные URL — требуют аутентификации.
     *  - Вставляет JwtAuthenticationFilter перед UsernamePasswordAuthenticationFilter.
     *
     * @param http     объект для настройки HTTP‑защиты
     * @param jwtFilter фильтр для обработки и валидации JWT
     * @return готовая цепочка фильтров SecurityFilterChain
     * @throws Exception при ошибках конфигурации
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
                                           JwtAuthenticationFilter jwtFilter) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Бин AuthenticationManager, использующий DAO‑провайдер:
     *  - UserDetailsService для загрузки пользователей из БД.
     *  - PasswordEncoder для проверки захешированных паролей.
     *
     * @param uds     сервис UserDetailsService (CustomUserDetailsService)
     * @param encoder PasswordEncoder (BCryptPasswordEncoder)
     * @return AuthenticationManager для аутентификации запросов
     */
    @Bean
    public AuthenticationManager authManager(UserDetailsService uds,
                                             PasswordEncoder encoder) {
        DaoAuthenticationProvider prov = new DaoAuthenticationProvider();
        prov.setUserDetailsService(uds);
        prov.setPasswordEncoder(encoder);
        return new ProviderManager(prov);
    }

    /**
     * Кодировщик паролей BCrypt с силой хеширования 12.
     * Используется как в аутентификации, так и при сохранении паролей.
     *
     * @return PasswordEncoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    /**
     * Инициализация дефолтных ролей в БД при старте приложения.
     * Если роли ADMIN, TERMINAL_OPERATOR, TALLYMAN отсутствуют — создаёт их.
     *
     * @param roleRepo репозиторий для работы с сущностью Role
     * @return CommandLineRunner, выполняющийся после старта контекста
     */
    @Bean
    public CommandLineRunner initRoles(RoleRepository roleRepo) {
        return args -> {
            List<String> names = List.of("ADMIN", "TERMINAL_OPERATOR", "TALLYMAN");
            for (String name : names) {
                if (!roleRepo.existsByRole(name)) {
                    roleRepo.save(new Role(null, name));
                }
            }
        };
  }

}

