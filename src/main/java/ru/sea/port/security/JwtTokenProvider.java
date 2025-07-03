package ru.sea.port.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.security.Key;

/**
 * Провайдер работы с JWT:
 *  - Генерирует токен с HS256 и сроком жизни.
 *  - Валидирует подпись и срок годности.
 *  - Извлекает username (email) из токена.
 *
 * Аннотации:
 *  - @Component: бин Spring.
 *  - @PostConstruct: после инъекций декодирует Base64‑секрет в Key.
 *
 * @see io.jsonwebtoken.Jwts
 * @see javax.annotation.PostConstruct
 */
@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String secretBase64;

    @Value("${jwt.expiration-ms}")
    private long validityInMs;

    private Key secretKey;

    /**
     * Инициализация HMAC‑ключа из Base64‑строки после создания бина.
     */
    @PostConstruct
    protected void init() {
        // декодируем Base64 и создаём ключ
        byte[] keyBytes = Decoders.BASE64.decode(secretBase64);
        secretKey = Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Создаёт JWT-токен для аутентифицированного пользователя.
     *
     * @param auth Authentication, содержащий Principal с UserDetails
     * @return подписанный JWT в формате String
     */
    public String createToken(Authentication auth) {
        UserDetails user = (UserDetails) auth.getPrincipal();
        Date now = new Date();
        Date expires = new Date(now.getTime() + validityInMs);

        return Jwts.builder()
            .setSubject(user.getUsername())
            .claim("roles", user.getAuthorities())
            .setIssuedAt(now)
            .setExpiration(expires)
            .signWith(secretKey, SignatureAlgorithm.HS256)
            .compact();
    }

    /**
     * Проверяет корректность и срок годности JWT-токена.
     *
     * @param token строка токена из заголовка HTTP
     * @return true, если токен валиден и не истёк
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            // можно логировать причину
            return false;
        }
    }


     /**
     * Извлекает subject (email) из тела токена.
     *
     * @param token корректный предварительно проверенный JWT
     * @return строка username/email пользователя
     */
    public String getUsername(String token) {
        Claims claims = Jwts.parserBuilder()
            .setSigningKey(secretKey)
            .build()
            .parseClaimsJws(token)
            .getBody();
        return claims.getSubject();
    }
}


