package com.ddang.ddang.device.application.dto.request;

import com.ddang.ddang.authentication.application.dto.request.RequestLoginDeviceTokenDto;
import com.ddang.ddang.device.presentation.dto.request.UpdateDeviceTokenRequest;
import java.util.Optional;
import javax.annotation.Nullable;

public record CreateDeviceTokenDto(@Nullable String deviceToken) {

    public static CreateDeviceTokenDto from(final UpdateDeviceTokenRequest updateDeviceTokenRequest) {
        return new CreateDeviceTokenDto(updateDeviceTokenRequest.deviceToken());
    }

    public static CreateDeviceTokenDto from(final RequestLoginDeviceTokenDto deviceTokenDto) {
        return new CreateDeviceTokenDto(deviceTokenDto.deviceToken());
    }

    public Optional<String> findDeviceToken() {
        return Optional.ofNullable(deviceToken);
    }
}
