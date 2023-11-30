package com.ddang.ddang.chat.presentation.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import javax.annotation.Nullable;

public record ReadMessageRequest(
        @Nullable
        Long messageReaderId,

        @NotNull(message = "채팅방 아이디가 입력되지 않았습니다.")
        @Positive(message = "채팅방 아이디는 양수입니다.")
        Long chatRoomId,

        @Nullable
        Long lastMessageId
) {
}
