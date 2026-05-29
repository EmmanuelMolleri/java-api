package br.com.java.api.application.mapper;

import br.com.java.api.application.dto.post.CommentResponse;
import br.com.java.api.application.dto.post.PostResponse;
import br.com.java.api.domain.entities.Post;
import br.com.java.api.domain.entities.PostComment;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PostMapper {

    public PostResponse toResponse(
        Post post,
        long likeCount,
        boolean likedByCurrentUser,
        List<PostComment> comments
    ) {
        List<CommentResponse> commentResponses = comments.stream()
            .map(this::toCommentResponse)
            .toList();

        return new PostResponse(
            post.getId(),
            post.getAuthor().getId(),
            post.getAuthor().getFullName(),
            post.getContent(),
            post.getVisibility(),
            post.getCreatedAt(),
            likeCount,
            likedByCurrentUser,
            commentResponses
        );
    }

    public CommentResponse toCommentResponse(PostComment comment) {
        return new CommentResponse(
            comment.getId(),
            comment.getAuthor().getId(),
            comment.getAuthor().getFullName(),
            comment.getContent(),
            comment.getCreatedAt()
        );
    }
}
