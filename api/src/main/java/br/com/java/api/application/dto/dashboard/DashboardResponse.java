package br.com.java.api.application.dto.dashboard;

import br.com.java.api.application.dto.friendship.FriendshipResponse;
import br.com.java.api.application.dto.user.UserSummaryResponse;

import java.util.List;

public record DashboardResponse(
    UserSummaryResponse me,
    List<FriendshipResponse> pendingFriendRequests
) {
}
