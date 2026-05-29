package br.com.java.api.controller;

import br.com.java.api.application.dto.friendship.FriendshipResponse;
import br.com.java.api.application.dto.user.UserSummaryResponse;
import br.com.java.api.application.service.FriendshipService;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Validated
@RequestMapping("/api/friendships")
public class FriendshipController {

    private final FriendshipService friendshipService;

    public FriendshipController(FriendshipService friendshipService) {
        this.friendshipService = friendshipService;
    }

    @PostMapping("/request/{targetUserId}")
    public ResponseEntity<FriendshipResponse> sendRequest(@PathVariable @Positive Long targetUserId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(friendshipService.sendRequest(targetUserId));
    }

    @PostMapping("/{friendshipId}/accept")
    public ResponseEntity<FriendshipResponse> acceptRequest(@PathVariable @Positive Long friendshipId) {
        return ResponseEntity.ok(friendshipService.acceptRequest(friendshipId));
    }

    @GetMapping("/pending")
    public ResponseEntity<List<FriendshipResponse>> listPendingRequests() {
        List<FriendshipResponse> pending = friendshipService.listPendingRequests();
        if (pending.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(pending);
    }

    @GetMapping
    public ResponseEntity<List<UserSummaryResponse>> listFriends(
        @RequestParam(name = "q", defaultValue = "") @Size(max = 255) String query
    ) {
        List<UserSummaryResponse> friends = friendshipService.listFriends(query);
        if (friends.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(friends);
    }

    @DeleteMapping("/{friendId}")
    public ResponseEntity<Void> removeFriendship(@PathVariable @Positive Long friendId) {
        friendshipService.removeFriendshipWith(friendId);
        return ResponseEntity.noContent().build();
    }
}
