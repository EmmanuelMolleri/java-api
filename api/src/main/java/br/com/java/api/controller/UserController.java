package br.com.java.api.controller;

import br.com.java.api.application.dto.post.PostResponse;
import br.com.java.api.application.dto.user.UserProfileResponse;
import br.com.java.api.application.dto.user.UpdateProfileRequest;
import br.com.java.api.application.dto.user.UserSummaryResponse;
import br.com.java.api.application.service.FriendshipService;
import br.com.java.api.application.service.PostService;
import br.com.java.api.application.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Validated
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final PostService postService;
    private final FriendshipService friendshipService;

    public UserController(UserService userService, PostService postService, FriendshipService friendshipService) {
        this.userService = userService;
        this.postService = postService;
        this.friendshipService = friendshipService;
    }

    @GetMapping("/me")
    public ResponseEntity<UserSummaryResponse> getMyProfile() {
        return ResponseEntity.ok(userService.getMyProfile());
    }

    @PatchMapping("/me")
    public ResponseEntity<UserSummaryResponse> updateMyProfile(@Valid @RequestBody UpdateProfileRequest request) {
        return ResponseEntity.ok(userService.updateMyProfile(request));
    }

    @GetMapping("/search")
    public ResponseEntity<List<UserSummaryResponse>> searchContacts(
        @RequestParam(name = "q", defaultValue = "") @Size(max = 255) String query
    ) {
        List<UserSummaryResponse> users = userService.searchContacts(query);
        if (users.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{userId}/profile")
    public ResponseEntity<UserProfileResponse> getProfile(
        @PathVariable @Positive Long userId,
        @RequestParam(defaultValue = "0") @Min(0) int page,
        @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size
    ) {
        UserSummaryResponse user = userService.getSummaryById(userId);
        Long currentUserId = userService.getCurrentUserId();
        boolean isFriend = friendshipService.areFriends(currentUserId, userId);
        Page<PostResponse> posts = postService.getPostsForProfile(userId, page, size);
        return ResponseEntity.ok(new UserProfileResponse(user, isFriend, posts));
    }
}
