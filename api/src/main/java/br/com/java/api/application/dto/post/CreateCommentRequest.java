package br.com.java.api.application.dto.post;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateCommentRequest(
    @NotBlank @Size(max = 500) String content
) {
}
