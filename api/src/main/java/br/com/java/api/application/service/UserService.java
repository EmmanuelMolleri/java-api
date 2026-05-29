package br.com.java.api.application.service;

import br.com.java.api.application.dto.user.UpdateProfileRequest;
import br.com.java.api.application.dto.user.UserSummaryResponse;
import br.com.java.api.application.mapper.UserMapper;
import br.com.java.api.domain.entities.AppUser;
import br.com.java.api.domain.model.ResourceNotFoundException;
import br.com.java.api.infrastructure.Repository.AppUserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {

    private final AppUserRepository userRepository;
    private final UserMapper userMapper;
    private final AuthenticatedUserService authenticatedUserService;

    public UserService(
        AppUserRepository userRepository,
        UserMapper userMapper,
        AuthenticatedUserService authenticatedUserService
    ) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.authenticatedUserService = authenticatedUserService;
    }

    public UserSummaryResponse getMyProfile() {
        return userMapper.toSummary(authenticatedUserService.getRequiredCurrentUser());
    }

    public Long getCurrentUserId() {
        return authenticatedUserService.getRequiredCurrentUser().getId();
    }

    @Transactional
    public UserSummaryResponse updateMyProfile(UpdateProfileRequest request) {
        AppUser currentUser = authenticatedUserService.getRequiredCurrentUser();
        currentUser.setFullName(request.fullName());
        currentUser.setNickname(request.nickname());
        currentUser.setProfileImage(request.profileImage());
        return userMapper.toSummary(userRepository.save(currentUser));
    }

    public List<UserSummaryResponse> searchContacts(String query) {
        AppUser currentUser = authenticatedUserService.getRequiredCurrentUser();
        String normalized = query == null ? "" : query.trim();

        return userRepository.findByFullNameContainingIgnoreCaseOrEmailContainingIgnoreCase(normalized, normalized)
            .stream()
            .filter(u -> !u.getId().equals(currentUser.getId()))
            .map(userMapper::toSummary)
            .toList();
    }

    public AppUser getById(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Usuario nao encontrado"));
    }

    public UserSummaryResponse getSummaryById(Long id) {
        return userMapper.toSummary(getById(id));
    }
}
