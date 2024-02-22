package com.ddang.ddang.authentication.presentation.dto.request;

import jakarta.validation.constraints.NotEmpty;
import javax.annotation.Nullable;

public record LoginTokenRequest(
        @NotEmpty(message = "AccessToken을 입력해주세요.")
        String accessToken,

        @Nullable
        String deviceToken
) {
}
