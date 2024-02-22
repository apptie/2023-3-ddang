package com.ddang.ddang.chat.presentation.dto.response;

import com.ddang.ddang.authentication.domain.dto.AuthenticationUserInfo;
import com.ddang.ddang.chat.application.dto.response.ReadMultipleMessageDto;

import java.time.LocalDateTime;

public record ReadMessageResponse(
        Long id,
        LocalDateTime createdTime,
        boolean isMyMessage,
        String content
) {

    public static ReadMessageResponse of(
            final ReadMultipleMessageDto readMultipleMessageDto,
            final AuthenticationUserInfo userInfo
    ) {
        return new ReadMessageResponse(
                readMultipleMessageDto.id(),
                readMultipleMessageDto.createdTime(),
                isMessageOwner(readMultipleMessageDto, userInfo),
                readMultipleMessageDto.content()
        );
    }

    private static boolean isMessageOwner(
            final ReadMultipleMessageDto readMultipleMessageDto,
            final AuthenticationUserInfo userInfo
    ) {
        return readMultipleMessageDto.writerId()
                                     .equals(userInfo.userId());
    }
}
