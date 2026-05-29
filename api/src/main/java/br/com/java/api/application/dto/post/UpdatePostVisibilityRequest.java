package br.com.java.api.application.dto.post;

import br.com.java.api.domain.Enums.PostVisibility;
import jakarta.validation.constraints.NotNull;

public record UpdatePostVisibilityRequest(@NotNull PostVisibility visibility) {
}
