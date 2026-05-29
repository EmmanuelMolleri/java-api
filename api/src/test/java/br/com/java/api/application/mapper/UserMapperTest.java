package br.com.java.api.application.mapper;

import br.com.java.api.application.dto.auth.RegisterUserRequest;
import br.com.java.api.domain.entities.AppUser;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserMapperTest {

    private final UserMapper userMapper = new UserMapper();

    @Test
    void shouldMapRegisterRequestToEntity() {
        RegisterUserRequest request = new RegisterUserRequest(
            "Usuario Teste",
            "USER@MAIL.COM",
            "apelido",
            LocalDate.of(2000, 1, 1),
            "secret",
            "http://img"
        );

        AppUser user = userMapper.toEntity(request);

        assertEquals("Usuario Teste", user.getFullName());
        assertEquals("user@mail.com", user.getEmail());
        assertEquals("apelido", user.getNickname());
        assertEquals(LocalDate.of(2000, 1, 1), user.getBirthDate());
        assertEquals("http://img", user.getProfileImage());
    }
}
