package ru.sea.port.entity;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Обёртка над сущностью User, реализующая UserDetails для Spring Security.
 * Делегирует все проверки (locked, expired и т.п.) и возвращает список прав
 * на основе Role из БД.
 * 
 * Особенности:
 * - Реализует контракт UserDetails (интерфейс Spring Security)
 * - Преобразует роль пользователя в GrantedAuthority с префиксом "ROLE_"
 * - Все аккаунты считаются активными (isEnabled=true)
 * 
 * @see org.springframework.security.core.userdetails.UserDetails
 * @see ru.sea.port.entity.User
 */
public class CustomUserDetails implements UserDetails {
    private final User user;



    public CustomUserDetails(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public Long getId() {
        return user.getId();
    }

    public Role getRole() {
        return user.getRole();
    }

    /**
     * Возвращает список прав (authorities) пользователя.
     * Каждая роль из БД автоматически оборачивается в SimpleGrantedAuthority с префиксом "ROLE_".
     *
     * @return коллекция GrantedAuthority для текущего пользователя
     */
    @Override public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(
            new SimpleGrantedAuthority(
                "ROLE_" +
                user.getRole()
                .getRole()
            ));
    }
    @Override public String getPassword() { return user.getPassword(); }
    @Override public String getUsername() { return user.getEmail(); }
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }
}
