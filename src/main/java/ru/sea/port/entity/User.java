package ru.sea.port.entity;

import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * Сущность пользователя системы.
 * 
 * Особенности:
 * - @ManyToOne: отношение "многие-к-одному" к роли (аналог внешнего ключа)
 * - @Builder: генератор билдера (паттерн)
 * - Пароль хранится в хешированном виде
 * 
 * @entity @Table(name="users")
 * @see Role
 */
@Entity
@Table(name="users")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="first_name")
    private String firstName;

    @Column(name="patronomic")
    private String patronomic;

    @Column(name="last_name")
    private String lastName;

    @Column(name="email")
    private String email;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;


    @Column(name = "password")
    private String password;
}
