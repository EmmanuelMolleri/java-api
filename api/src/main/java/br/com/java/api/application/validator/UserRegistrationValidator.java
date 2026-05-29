package br.com.java.api.application.validator;

import br.com.java.api.domain.model.ConflictException;
import br.com.java.api.infrastructure.Repository.AppUserRepository;
import org.springframework.stereotype.Component;

@Component
public class UserRegistrationValidator {

    private final AppUserRepository userRepository;

    public UserRegistrationValidator(AppUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void validateUniqueEmail(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new ConflictException("E-mail ja cadastrado");
        }
    }
}
