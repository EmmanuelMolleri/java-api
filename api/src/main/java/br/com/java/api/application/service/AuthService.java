package br.com.java.api.application.service;

import br.com.java.api.application.dto.auth.AuthResponse;
import br.com.java.api.application.dto.auth.LoginRequest;
import br.com.java.api.application.dto.auth.RegisterUserRequest;
import br.com.java.api.application.mapper.UserMapper;
import br.com.java.api.application.validator.UserRegistrationValidator;
import br.com.java.api.domain.entities.AppUser;
import br.com.java.api.infrastructure.Repository.AppUserRepository;
import br.com.java.api.infrastructure.Service.JwtService;
import br.com.java.api.infrastructure.security.JwtProperties;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final AppUserRepository userRepository;
    private final UserMapper userMapper;
    private final UserRegistrationValidator userRegistrationValidator;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final JwtProperties jwtProperties;

    public AuthService(
        AppUserRepository userRepository,
        UserMapper userMapper,
        UserRegistrationValidator userRegistrationValidator,
        PasswordEncoder passwordEncoder,
        AuthenticationManager authenticationManager,
        JwtService jwtService,
        JwtProperties jwtProperties
    ) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.userRegistrationValidator = userRegistrationValidator;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.jwtProperties = jwtProperties;
    }

    @Transactional
    public AuthResponse register(RegisterUserRequest request) {
        userRegistrationValidator.validateUniqueEmail(request.email().toLowerCase());

        AppUser user = userMapper.toEntity(request);
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        userRepository.save(user);

        String token = jwtService.generateToken(user.getEmail());
        return new AuthResponse(token, "Bearer", jwtProperties.expirationSeconds());
    }

    public AuthResponse login(LoginRequest request) {
        try {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email().toLowerCase(), request.password())
            );
        } catch (AuthenticationException ex) {
            throw new BadCredentialsException("E-mail ou senha invalidos", ex);
        }

        String token = jwtService.generateToken(request.email().toLowerCase());
        return new AuthResponse(token, "Bearer", jwtProperties.expirationSeconds());
    }
}
