package ru.sea.port.security;

import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.*;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.sea.port.entity.CustomUserDetails;
import ru.sea.port.entity.User;
import ru.sea.port.repository.UserRepository;


/**
 * Сервис для загрузки пользователя по username (email) в контексте Spring Security.
 * Используется внутри JwtAuthenticationFilter и DaoAuthenticationProvider.
 *
 * @see UserDetailsService
 * @throws UsernameNotFoundException если пользователь с таким email не найден
 */
@Service
@AllArgsConstructor
@Getter
@Setter
public class CustomUserDetailsService implements UserDetailsService {
    
    
    private final UserRepository userRepo;

    @Override
    public UserDetails loadUserByUsername(String email) {
        User user = userRepo.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

        return new CustomUserDetails(user);
    }
}

