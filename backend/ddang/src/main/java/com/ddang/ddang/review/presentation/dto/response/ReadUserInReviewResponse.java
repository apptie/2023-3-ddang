package com.ddang.ddang.review.presentation.dto.response;

import com.ddang.ddang.review.application.dto.ReadUserInReviewDto;

public record ReadUserInReviewResponse(Long id, String name, String profileImage) {

    public static ReadUserInReviewResponse of(
            final ReadUserInReviewDto userDto,
            final String imageRelativeUrl
    ) {
        return new ReadUserInReviewResponse(
                userDto.id(),
                userDto.name(),
                imageRelativeUrl + userDto.profileImageStoreName()
        );
    }
}
