package br.com.java.api.application.dto.post;

import br.com.java.api.domain.Enums.PostVisibility;

import java.time.LocalDateTime;
import java.util.List;

public record PostResponse(
    Long id,
    Long authorId,
    String authorName,
    String content,
    PostVisibility visibility,
    LocalDateTime createdAt,
    long likeCount,
    boolean likedByCurrentUser,
    List<CommentResponse> comments
) {
}
