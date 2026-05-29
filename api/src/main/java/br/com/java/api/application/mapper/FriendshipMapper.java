package br.com.java.api.application.mapper;

import br.com.java.api.application.dto.friendship.FriendshipResponse;
import br.com.java.api.domain.entities.Friendship;
import org.springframework.stereotype.Component;

@Component
public class FriendshipMapper {

    public FriendshipResponse toResponse(Friendship friendship) {
        return new FriendshipResponse(
            friendship.getId(),
            friendship.getRequester().getId(),
            friendship.getRequester().getFullName(),
            friendship.getAddressee().getId(),
            friendship.getAddressee().getFullName(),
            friendship.getStatus(),
            friendship.getCreatedAt()
        );
    }
}
