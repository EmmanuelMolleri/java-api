package br.com.java.api.application.service;

import br.com.java.api.application.dto.friendship.FriendshipResponse;
import br.com.java.api.application.dto.user.UserSummaryResponse;
import br.com.java.api.application.mapper.FriendshipMapper;
import br.com.java.api.application.mapper.UserMapper;
import br.com.java.api.application.validator.FriendshipValidator;
import br.com.java.api.domain.entities.AppUser;
import br.com.java.api.domain.entities.Friendship;
import br.com.java.api.domain.Enums.FriendshipStatus;
import br.com.java.api.domain.model.ForbiddenOperationException;
import br.com.java.api.domain.model.ResourceNotFoundException;
import br.com.java.api.infrastructure.Repository.FriendshipRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class FriendshipService {

    private final FriendshipRepository friendshipRepository;
    private final AuthenticatedUserService authenticatedUserService;
    private final UserService userService;
    private final FriendshipMapper friendshipMapper;
    private final UserMapper userMapper;
    private final FriendshipValidator friendshipValidator;

    public FriendshipService(
        FriendshipRepository friendshipRepository,
        AuthenticatedUserService authenticatedUserService,
        UserService userService,
        FriendshipMapper friendshipMapper,
        UserMapper userMapper,
        FriendshipValidator friendshipValidator
    ) {
        this.friendshipRepository = friendshipRepository;
        this.authenticatedUserService = authenticatedUserService;
        this.userService = userService;
        this.friendshipMapper = friendshipMapper;
        this.userMapper = userMapper;
        this.friendshipValidator = friendshipValidator;
    }

    @Transactional
    public FriendshipResponse sendRequest(Long targetUserId) {
        AppUser currentUser = authenticatedUserService.getRequiredCurrentUser();
        friendshipValidator.validateNotSelfRequest(currentUser.getId(), targetUserId);

        AppUser target = userService.getById(targetUserId);
        Optional<Friendship> existing = friendshipRepository.findRelationshipBetween(currentUser.getId(), target.getId());
        friendshipValidator.validateRequestCanBeCreated(existing.orElse(null));

        Friendship friendship = existing.orElseGet(Friendship::new);
        friendship.setRequester(currentUser);
        friendship.setAddressee(target);
        friendship.setStatus(FriendshipStatus.PENDING);

        return friendshipMapper.toResponse(friendshipRepository.save(friendship));
    }

    @Transactional
    public FriendshipResponse acceptRequest(Long friendshipId) {
        AppUser currentUser = authenticatedUserService.getRequiredCurrentUser();
        Friendship friendship = friendshipRepository.findById(friendshipId)
            .orElseThrow(() -> new ResourceNotFoundException("Solicitacao de amizade nao encontrada"));

        if (!friendship.getAddressee().getId().equals(currentUser.getId())) {
            throw new ForbiddenOperationException("Somente o destinatario pode aceitar a solicitacao");
        }

        friendship.setStatus(FriendshipStatus.ACCEPTED);
        return friendshipMapper.toResponse(friendshipRepository.save(friendship));
    }

    public List<FriendshipResponse> listPendingRequests() {
        AppUser currentUser = authenticatedUserService.getRequiredCurrentUser();
        return friendshipRepository.findByAddresseeIdAndStatus(currentUser.getId(), FriendshipStatus.PENDING)
            .stream()
            .map(friendshipMapper::toResponse)
            .toList();
    }

    public List<UserSummaryResponse> listFriends(String query) {
        AppUser currentUser = authenticatedUserService.getRequiredCurrentUser();
        String normalized = query == null ? "" : query.trim().toLowerCase();

        List<Friendship> relationships = friendshipRepository.findAllByUserAndStatus(currentUser.getId(), FriendshipStatus.ACCEPTED);
        List<UserSummaryResponse> friends = new ArrayList<>();
        for (Friendship relationship : relationships) {
            AppUser friend = relationship.getRequester().getId().equals(currentUser.getId())
                ? relationship.getAddressee()
                : relationship.getRequester();

            String name = friend.getFullName() == null ? "" : friend.getFullName().toLowerCase();
            String email = friend.getEmail() == null ? "" : friend.getEmail().toLowerCase();
            if (normalized.isEmpty() || name.contains(normalized) || email.contains(normalized)) {
                friends.add(userMapper.toSummary(friend));
            }
        }

        return friends;
    }

    public boolean areFriends(Long userA, Long userB) {
        return friendshipRepository.findRelationshipBetween(userA, userB)
            .map(f -> f.getStatus() == FriendshipStatus.ACCEPTED)
            .orElse(false);
    }

    @Transactional
    public void removeFriendshipWith(Long friendId) {
        AppUser currentUser = authenticatedUserService.getRequiredCurrentUser();
        Friendship friendship = friendshipRepository.findRelationshipBetween(currentUser.getId(), friendId)
            .orElseThrow(() -> new ResourceNotFoundException("Vinculo de amizade nao encontrado"));

        if (friendship.getStatus() != FriendshipStatus.ACCEPTED && friendship.getStatus() != FriendshipStatus.PENDING) {
            throw new ResourceNotFoundException("Vinculo de amizade nao encontrado");
        }

        friendshipRepository.delete(friendship);
    }
}
