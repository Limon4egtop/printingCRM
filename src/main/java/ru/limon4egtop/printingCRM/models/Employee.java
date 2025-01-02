package ru.limon4egtop.printingCRM.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Представляет пользователя в системе с деталями безопасности.
 */
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Employee implements UserDetails {
    // Разделитель, используемый для разделения строки с полномочиями
    private static final String AUTHORITIES_DELIMITER = "::";

    // Уникальный идентификатор пользователя
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Имя пользователя
    private String username;

    // Пароль пользователя
    private String password;

    // Полномочия, предоставленные пользователю, хранятся в виде одной строки
    private String authorities;

    private String firstName;
    private String lastName;

    /**
     * Возвращает полномочия, предоставленные пользователю.
     * @return коллекция объектов GrantedAuthority
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Разделяет строку полномочий и преобразует в список объектов SimpleGrantedAuthority
        return Arrays.stream(this.authorities.split(AUTHORITIES_DELIMITER))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    /**
     * Возвращает пароль, используемый для аутентификации пользователя.
     * @return пароль
     */
    @Override
    public String getPassword() {
        return password;
    }

    /**
     * Возвращает имя пользователя, используемое для аутентификации.
     * @return имя пользователя
     */
    @Override
    public String getUsername() {
        return username;
    }

    /**
     * Указывает, истек ли срок действия учетной записи пользователя.
     * @return true, если учетная запись не просрочена, иначе false
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * Указывает, заблокирован ли пользователь.
     * @return true, если учетная запись не заблокирована, иначе false
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * Указывает, истек ли срок действия учетных данных пользователя.
     * @return true, если учетные данные не просрочены, иначе false
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * Указывает, включен ли пользователь.
     * @return true, если пользователь включен, иначе false
     */
    @Override
    public boolean isEnabled() {
        return true;
    }
}