package br.com.java.api.application.validator;

import br.com.java.api.domain.entities.Friendship;
import br.com.java.api.domain.Enums.FriendshipStatus;
import br.com.java.api.domain.model.BadRequestException;
import br.com.java.api.domain.model.ConflictException;
import org.springframework.stereotype.Component;

@Component
public class FriendshipValidator {

    public void validateNotSelfRequest(Long requesterId, Long targetId) {
        if (requesterId.equals(targetId)) {
            throw new BadRequestException("Nao e permitido enviar solicitacao para si mesmo");
        }
    }

    public void validateRequestCanBeCreated(Friendship existing) {
        if (existing == null) {
            return;
        }

        if (existing.getStatus() == FriendshipStatus.ACCEPTED) {
            throw new ConflictException("Usuarios ja sao amigos");
        }

        if (existing.getStatus() == FriendshipStatus.PENDING) {
            throw new ConflictException("Ja existe uma solicitacao pendente entre os usuarios");
        }
    }
}
