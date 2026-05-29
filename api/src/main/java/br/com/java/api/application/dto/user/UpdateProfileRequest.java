package br.com.java.api.application.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateProfileRequest(
    @NotBlank @Size(max = 255) String fullName,
    @Size(max = 50) String nickname,
    @Size(max = 512) String profileImage
) {
}
