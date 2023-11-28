package com.ddang.ddang.user.presentation.dto.response;

import com.ddang.ddang.user.application.dto.request.ReadUserDto;

public record ReadUserResponse(String name, String profileImage, Float reliability) {

    public static ReadUserResponse of(final ReadUserDto readUserDto, final String imageRelativeUrl) {
        return new ReadUserResponse(
                readUserDto.name(),
                imageRelativeUrl + readUserDto.profileImageStoreName(),
                readUserDto.reliability()
        );
    }
}
