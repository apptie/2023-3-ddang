package com.ddang.ddang.review.presentation.dto.response;

import com.ddang.ddang.review.application.dto.response.ReadReviewDetailDto;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
class ReadReviewDetailResponseTest {

    @Test
    void dto의_필드가_null일_때_response의_필드도_null이다() {
        // given
        final ReadReviewDetailDto readReviewDetailDto = new ReadReviewDetailDto(null, null);

        // when
        final ReadReviewDetailResponse actual = ReadReviewDetailResponse.from(readReviewDetailDto);

        // then
        SoftAssertions.assertSoftly(softAssertions -> {
            softAssertions.assertThat(actual.score()).isNull();
            softAssertions.assertThat(actual.content()).isNull();
        });
    }
}
