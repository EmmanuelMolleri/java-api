package br.com.java.api.application.service;

import br.com.java.api.application.dto.auth.AuthResponse;
import br.com.java.api.application.dto.auth.RegisterUserRequest;
import br.com.java.api.application.mapper.UserMapper;
import br.com.java.api.application.validator.UserRegistrationValidator;
import br.com.java.api.domain.entities.AppUser;
import br.com.java.api.infrastructure.Repository.AppUserRepository;
import br.com.java.api.infrastructure.Service.JwtService;
import br.com.java.api.infrastructure.security.JwtProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AppUserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private UserRegistrationValidator userRegistrationValidator;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @Mock
    private JwtProperties jwtProperties;

    @InjectMocks
    private AuthService authService;

    @Test
    void shouldEncodePasswordAndReturnTokenOnRegister() {
        RegisterUserRequest request = new RegisterUserRequest(
            "Fulano",
            "fulano@mail.com",
            null,
            LocalDate.of(1999, 1, 1),
            "senha123",
            null
        );

        AppUser user = new AppUser();
        user.setEmail("fulano@mail.com");

        when(userMapper.toEntity(request)).thenReturn(user);
        when(passwordEncoder.encode("senha123")).thenReturn("hash");
        when(jwtService.generateToken("fulano@mail.com")).thenReturn("token");
        when(jwtProperties.expirationSeconds()).thenReturn(7200L);

        AuthResponse response = authService.register(request);

        ArgumentCaptor<AppUser> captor = ArgumentCaptor.forClass(AppUser.class);
        verify(userRepository).save(captor.capture());
        assertEquals("hash", captor.getValue().getPasswordHash());
        assertEquals("token", response.accessToken());
        assertEquals(7200L, response.expiresInSeconds());
    }
}
