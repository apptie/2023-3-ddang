package com.ddang.ddang.chat.presentation.dto.response;

import com.ddang.ddang.chat.application.dto.response.ReadMultipleMessageDto;

import java.time.LocalDateTime;

public record ReadMessageResponse(
        Long id,
        LocalDateTime createdTime,
        boolean isMyMessage,
        String content
) {

    public static ReadMessageResponse of(final ReadMultipleMessageDto readMultipleMessageDto, final boolean isMyMessage) {
        return new ReadMessageResponse(
                readMultipleMessageDto.id(),
                readMultipleMessageDto.createdTime(),
                isMyMessage,
                readMultipleMessageDto.content()
        );
    }
}
