package br.com.java.api.application.validator;

import br.com.java.api.domain.entities.Friendship;
import br.com.java.api.domain.Enums.FriendshipStatus;
import br.com.java.api.domain.model.BadRequestException;
import br.com.java.api.domain.model.ConflictException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FriendshipValidatorTest {

    private final FriendshipValidator validator = new FriendshipValidator();

    @Test
    void shouldRejectSelfFriendRequest() {
        assertThrows(BadRequestException.class, () -> validator.validateNotSelfRequest(1L, 1L));
    }

    @Test
    void shouldRejectAcceptedRelationship() {
        Friendship friendship = new Friendship();
        friendship.setStatus(FriendshipStatus.ACCEPTED);

        assertThrows(ConflictException.class, () -> validator.validateRequestCanBeCreated(friendship));
    }

    @Test
    void shouldAllowWhenNoRelationship() {
        assertDoesNotThrow(() -> validator.validateRequestCanBeCreated(null));
    }
}
