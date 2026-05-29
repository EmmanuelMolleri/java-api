package br.com.java.api.application.dto.user;

import java.time.LocalDate;

public record UserSummaryResponse(
    Long id,
    String fullName,
    String email,
    String nickname,
    LocalDate birthDate,
    String profileImage
) {
}
