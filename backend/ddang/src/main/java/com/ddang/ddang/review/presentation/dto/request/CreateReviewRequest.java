package com.ddang.ddang.review.presentation.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import javax.annotation.Nullable;

public record CreateReviewRequest(

        @NotNull(message = "경매 아이디가 입력되지 않았습니다.")
        @Positive(message = "경매 아이디는 양수여야 합니다.")
        Long auctionId,

        @NotNull(message = "평가할 상대방의 아이디가 입력되지 않았습니다.")
        @Positive(message = "사용자 아이디는 양수여야 합니다.")
        Long targetId,

        @Nullable
        String content,

        @Positive(message = "점수는 양수여야 합니다.")
        float score
) {
}
