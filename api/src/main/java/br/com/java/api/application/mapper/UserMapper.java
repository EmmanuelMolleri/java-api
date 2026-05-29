package br.com.java.api.application.mapper;

import br.com.java.api.application.dto.auth.RegisterUserRequest;
import br.com.java.api.application.dto.user.UserSummaryResponse;
import br.com.java.api.domain.entities.AppUser;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public AppUser toEntity(RegisterUserRequest request) {
        AppUser user = new AppUser();
        user.setFullName(request.fullName());
        user.setEmail(request.email().toLowerCase());
        user.setNickname(request.nickname());
        user.setBirthDate(request.birthDate());
        user.setProfileImage(request.profileImage());
        return user;
    }

    public UserSummaryResponse toSummary(AppUser user) {
        return new UserSummaryResponse(
            user.getId(),
            user.getFullName(),
            user.getEmail(),
            user.getNickname(),
            user.getBirthDate(),
            user.getProfileImage()
        );
    }
}
