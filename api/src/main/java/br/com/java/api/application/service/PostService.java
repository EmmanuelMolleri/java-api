package br.com.java.api.application.service;

import br.com.java.api.application.dto.post.CommentResponse;
import br.com.java.api.application.dto.post.CreateCommentRequest;
import br.com.java.api.application.dto.post.CreatePostRequest;
import br.com.java.api.application.dto.post.PostResponse;
import br.com.java.api.application.dto.post.UpdatePostVisibilityRequest;
import br.com.java.api.application.mapper.PostMapper;
import br.com.java.api.domain.entities.AppUser;
import br.com.java.api.domain.entities.Post;
import br.com.java.api.domain.entities.PostComment;
import br.com.java.api.domain.entities.PostLike;
import br.com.java.api.domain.Enums.FriendshipStatus;
import br.com.java.api.domain.Enums.PostVisibility;
import br.com.java.api.domain.model.ForbiddenOperationException;
import br.com.java.api.domain.model.ResourceNotFoundException;
import br.com.java.api.infrastructure.Repository.FriendshipRepository;
import br.com.java.api.infrastructure.Repository.PostCommentRepository;
import br.com.java.api.infrastructure.Repository.PostLikeRepository;
import br.com.java.api.infrastructure.Repository.PostRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;
    private final PostCommentRepository postCommentRepository;
    private final FriendshipRepository friendshipRepository;
    private final AuthenticatedUserService authenticatedUserService;
    private final UserService userService;
    private final FriendshipService friendshipService;
    private final PostMapper postMapper;

    public PostService(
        PostRepository postRepository,
        PostLikeRepository postLikeRepository,
        PostCommentRepository postCommentRepository,
        FriendshipRepository friendshipRepository,
        AuthenticatedUserService authenticatedUserService,
        UserService userService,
        FriendshipService friendshipService,
        PostMapper postMapper
    ) {
        this.postRepository = postRepository;
        this.postLikeRepository = postLikeRepository;
        this.postCommentRepository = postCommentRepository;
        this.friendshipRepository = friendshipRepository;
        this.authenticatedUserService = authenticatedUserService;
        this.userService = userService;
        this.friendshipService = friendshipService;
        this.postMapper = postMapper;
    }

    @Transactional
    public PostResponse createPost(CreatePostRequest request) {
        AppUser currentUser = authenticatedUserService.getRequiredCurrentUser();

        Post post = new Post();
        post.setAuthor(currentUser);
        post.setContent(request.content());
        post.setVisibility(request.visibility());

        Post saved = postRepository.save(post);
        return postMapper.toResponse(saved, 0, false, List.of());
    }

    public Page<PostResponse> getFeed(int page, int size) {
        AppUser currentUser = authenticatedUserService.getRequiredCurrentUser();

        Set<Long> authorIds = new HashSet<>();
        authorIds.add(currentUser.getId());
        friendshipRepository.findAllByUserAndStatus(currentUser.getId(), FriendshipStatus.ACCEPTED)
            .forEach(relationship -> {
                if (relationship.getRequester().getId().equals(currentUser.getId())) {
                    authorIds.add(relationship.getAddressee().getId());
                } else {
                    authorIds.add(relationship.getRequester().getId());
                }
            });

        Pageable pageable = PageRequest.of(page, size);
        return postRepository.findFeedPosts(authorIds, pageable)
            .map(post -> toPostResponse(post, currentUser.getId()));
    }

    public Page<PostResponse> getPostsForProfile(Long targetUserId, int page, int size) {
        AppUser currentUser = authenticatedUserService.getRequiredCurrentUser();
        userService.getById(targetUserId);

        Pageable pageable = PageRequest.of(page, size);
        boolean isOwner = currentUser.getId().equals(targetUserId);
        boolean areFriends = friendshipService.areFriends(currentUser.getId(), targetUserId);

        if (isOwner || areFriends) {
            return postRepository.findByAuthorId(targetUserId, pageable)
                .map(post -> toPostResponse(post, currentUser.getId()));
        }

        return postRepository.findByAuthorAndVisibility(targetUserId, PostVisibility.PUBLIC, pageable)
            .map(post -> toPostResponse(post, currentUser.getId()));
    }

    @Transactional
    public PostResponse updateVisibility(Long postId, UpdatePostVisibilityRequest request) {
        AppUser currentUser = authenticatedUserService.getRequiredCurrentUser();
        Post post = getPostOrThrow(postId);

        if (!post.getAuthor().getId().equals(currentUser.getId())) {
            throw new ForbiddenOperationException("Somente o autor pode atualizar a visibilidade do post");
        }

        post.setVisibility(request.visibility());
        Post saved = postRepository.save(post);
        return toPostResponse(saved, currentUser.getId());
    }

    @Transactional
    public void likePost(Long postId) {
        AppUser currentUser = authenticatedUserService.getRequiredCurrentUser();
        Post post = getPostOrThrow(postId);
        ensureUserCanViewPost(currentUser, post);

        if (!postLikeRepository.existsByPostIdAndUserId(postId, currentUser.getId())) {
            PostLike like = new PostLike();
            like.setPost(post);
            like.setUser(currentUser);
            postLikeRepository.save(like);
        }
    }

    @Transactional
    public void unlikePost(Long postId) {
        AppUser currentUser = authenticatedUserService.getRequiredCurrentUser();
        Post post = getPostOrThrow(postId);
        ensureUserCanViewPost(currentUser, post);

        postLikeRepository.deleteByPostIdAndUserId(postId, currentUser.getId());
    }

    @Transactional
    public CommentResponse addComment(Long postId, CreateCommentRequest request) {
        AppUser currentUser = authenticatedUserService.getRequiredCurrentUser();
        Post post = getPostOrThrow(postId);
        ensureUserCanViewPost(currentUser, post);

        PostComment comment = new PostComment();
        comment.setPost(post);
        comment.setAuthor(currentUser);
        comment.setContent(request.content());

        PostComment saved = postCommentRepository.save(comment);
        return postMapper.toCommentResponse(saved);
    }

    private PostResponse toPostResponse(Post post, Long currentUserId) {
        List<PostComment> comments = postCommentRepository.findByPostIdOrderByCreatedAtAsc(post.getId());
        long likeCount = postLikeRepository.countByPostId(post.getId());
        boolean likedByCurrentUser = postLikeRepository.existsByPostIdAndUserId(post.getId(), currentUserId);
        return postMapper.toResponse(post, likeCount, likedByCurrentUser, comments);
    }

    private Post getPostOrThrow(Long postId) {
        return postRepository.findById(postId)
            .orElseThrow(() -> new ResourceNotFoundException("Post nao encontrado"));
    }

    private void ensureUserCanViewPost(AppUser user, Post post) {
        if (post.getAuthor().getId().equals(user.getId())) {
            return;
        }

        if (post.getVisibility() == PostVisibility.PUBLIC) {
            return;
        }

        if (!friendshipService.areFriends(user.getId(), post.getAuthor().getId())) {
            throw new ForbiddenOperationException("Sem permissao para acessar este post privado");
        }
    }
}
