package br.com.java.api.application.dto.post;

import br.com.java.api.domain.Enums.PostVisibility;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreatePostRequest(
    @NotBlank @Size(max = 2000) String content,
    @NotNull PostVisibility visibility
) {
}
