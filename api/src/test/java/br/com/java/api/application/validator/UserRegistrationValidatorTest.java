package br.com.java.api.application.validator;

import br.com.java.api.domain.model.ConflictException;
import br.com.java.api.infrastructure.Repository.AppUserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserRegistrationValidatorTest {

    @Mock
    private AppUserRepository userRepository;

    @InjectMocks
    private UserRegistrationValidator validator;

    @Test
    void shouldThrowWhenEmailAlreadyExists() {
        when(userRepository.existsByEmail("mail@test.com")).thenReturn(true);

        assertThrows(ConflictException.class, () -> validator.validateUniqueEmail("mail@test.com"));
    }

    @Test
    void shouldPassWhenEmailIsAvailable() {
        when(userRepository.existsByEmail("mail@test.com")).thenReturn(false);

        assertDoesNotThrow(() -> validator.validateUniqueEmail("mail@test.com"));
    }
}
