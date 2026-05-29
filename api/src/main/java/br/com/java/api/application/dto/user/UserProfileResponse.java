package br.com.java.api.application.dto.user;

import br.com.java.api.application.dto.post.PostResponse;
import org.springframework.data.domain.Page;

public record UserProfileResponse(
    UserSummaryResponse user,
    boolean friend,
    Page<PostResponse> posts
) {
}
