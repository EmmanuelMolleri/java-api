package br.com.java.api.application.dto.friendship;

import br.com.java.api.domain.Enums.FriendshipStatus;

import java.time.LocalDateTime;

public record FriendshipResponse(
    Long friendshipId,
    Long requesterId,
    String requesterName,
    Long addresseeId,
    String addresseeName,
    FriendshipStatus status,
    LocalDateTime createdAt
) {
}
