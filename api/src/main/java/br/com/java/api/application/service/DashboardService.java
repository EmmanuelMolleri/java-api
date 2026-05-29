package br.com.java.api.application.service;

import br.com.java.api.application.dto.dashboard.DashboardResponse;
import br.com.java.api.application.mapper.UserMapper;
import org.springframework.stereotype.Service;

@Service
public class DashboardService {

    private final AuthenticatedUserService authenticatedUserService;
    private final FriendshipService friendshipService;
    private final UserMapper userMapper;

    public DashboardService(
        AuthenticatedUserService authenticatedUserService,
        FriendshipService friendshipService,
        UserMapper userMapper
    ) {
        this.authenticatedUserService = authenticatedUserService;
        this.friendshipService = friendshipService;
        this.userMapper = userMapper;
    }

    public DashboardResponse getDashboard() {
        return new DashboardResponse(
            userMapper.toSummary(authenticatedUserService.getRequiredCurrentUser()),
            friendshipService.listPendingRequests()
        );
    }
}
