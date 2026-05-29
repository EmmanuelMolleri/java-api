package br.com.java.api.controller;

import br.com.java.api.application.dto.post.CommentResponse;
import br.com.java.api.application.dto.post.CreateCommentRequest;
import br.com.java.api.application.dto.post.CreatePostRequest;
import br.com.java.api.application.dto.post.PostResponse;
import br.com.java.api.application.dto.post.UpdatePostVisibilityRequest;
import br.com.java.api.application.service.PostService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping
    public ResponseEntity<PostResponse> createPost(@Valid @RequestBody CreatePostRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(postService.createPost(request));
    }

    @GetMapping("/feed")
    public ResponseEntity<Page<PostResponse>> getFeed(
        @RequestParam(defaultValue = "0") @Min(0) int page,
        @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size
    ) {
        Page<PostResponse> feed = postService.getFeed(page, size);
        if (feed.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(feed);
    }

    @PatchMapping("/{postId}/visibility")
    public ResponseEntity<PostResponse> updateVisibility(
        @PathVariable @Positive Long postId,
        @Valid @RequestBody UpdatePostVisibilityRequest request
    ) {
        return ResponseEntity.ok(postService.updateVisibility(postId, request));
    }

    @PostMapping("/{postId}/like")
    public ResponseEntity<Void> likePost(@PathVariable @Positive Long postId) {
        postService.likePost(postId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{postId}/like")
    public ResponseEntity<Void> unlikePost(@PathVariable @Positive Long postId) {
        postService.unlikePost(postId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{postId}/comments")
    public ResponseEntity<CommentResponse> addComment(
        @PathVariable @Positive Long postId,
        @Valid @RequestBody CreateCommentRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(postService.addComment(postId, request));
    }
}
