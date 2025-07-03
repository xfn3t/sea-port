package ru.sea.port.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ru.sea.port.entity.User;

/**
 * Репозиторий для CRUD‑операций над сущностью User и поиска по email.
 * Наследует методы JpaRepository для стандартных операций с таблицей users.
 *
 * Предоставляет дополнительный метод findByEmail:
 *  - Возвращает Optional<User>, что удобно для проверки наличия пользователя
 *    и обработки случая отсутствия через orElseThrow.
 *  - Используется в аутентификации (CustomUserDetailsService) и при регистрации,
 *    чтобы убедиться, что email уникален.
 *
 * @see org.springframework.data.jpa.repository.JpaRepository
 * @see com.example.yourpackage.model.User
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Ищет пользователя по полю email.
     * Позволяет работать с Optional, чтобы явно обрабатывать отсутствие.
     *
     * SQL эквивалент:
     *   SELECT * FROM users WHERE email = :email LIMIT 1;
     *
     * @param email уникальный адрес пользователя
     * @return Optional с найденной сущностью User или пустой Optional
     */
    Optional<User> findByEmail(String email);
}
