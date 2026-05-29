package br.com.java.api.application.dto.post;

import java.time.LocalDateTime;

public record CommentResponse(
    Long id,
    Long authorId,
    String authorName,
    String content,
    LocalDateTime createdAt
) {
}
