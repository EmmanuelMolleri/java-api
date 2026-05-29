package br.com.java.api.application.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record RegisterUserRequest(
    @NotBlank @Size(max = 255) String fullName,
    @NotBlank @Email @Size(max = 255) String email,
    @Size(max = 50) String nickname,
    @NotNull LocalDate birthDate,
    @NotBlank @Size(max = 128) String password,
    @Size(max = 512) String profileImage
) {
}
