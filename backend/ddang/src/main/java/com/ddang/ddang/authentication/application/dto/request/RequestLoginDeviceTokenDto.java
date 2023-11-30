package com.ddang.ddang.authentication.application.dto.request;

import com.ddang.ddang.authentication.presentation.dto.request.LoginTokenRequest;

public record RequestLoginDeviceTokenDto(String deviceToken) {

    public static RequestLoginDeviceTokenDto from(final LoginTokenRequest request) {
        return new RequestLoginDeviceTokenDto(request.deviceToken());
    }
}
