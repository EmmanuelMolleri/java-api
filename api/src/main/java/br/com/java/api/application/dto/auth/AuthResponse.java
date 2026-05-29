package br.com.java.api.application.dto.auth;

public record AuthResponse(
    String accessToken,
    String tokenType,
    long expiresInSeconds
) {
}
