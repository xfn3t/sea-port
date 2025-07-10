package ru.sea.port.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ru.sea.port.model.user.Role;

/**
 * Репозиторий для CRUD‑операций над сущностью Role.
 * Наследует методы JpaRepository, которые автоматически реализуют
 * базовые операции (save, findAll, findById, delete и т.д.).
 *
 * Также содержит кастомный метод existsByRole, который по имени роли
 * проверяет её существование в базе:
 *  - Spring Data генерирует реализацию на основе имени метода.
 *  - Используется в CommandLineRunner для инициализации дефолтных ролей.
 *
 * @see org.springframework.data.jpa.repository.JpaRepository
 * @see com.example.yourpackage.model.Role
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    /**
     * Проверяет, есть ли в таблице roles запись с указанным значением поля role.
     *
     * SQL эквивалент:
     *   SELECT CASE WHEN COUNT(*) > 0 THEN TRUE ELSE FALSE END
     *   FROM roles WHERE role = :role;
     *
     * @param role строка с именем роли (например, "ADMIN")
     * @return true, если хотя бы одна запись найдена
     */
    boolean existsByRole(String role);
}
