package com.ddang.ddang.report.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreateChatRoomReportRequest(
        @NotNull(message = "채팅방 아이디가 입력되지 않았습니다.")
        @Positive(message = "채팅방 아이디는 양수여야 합니다.")
        Long chatRoomId,

        @NotBlank(message = "신고 내용이 입력되지 않았습니다.")
        String description
) {
}
