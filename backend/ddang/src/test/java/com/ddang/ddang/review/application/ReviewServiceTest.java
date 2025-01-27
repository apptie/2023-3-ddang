package com.ddang.ddang.review.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import com.ddang.ddang.auction.infrastructure.persistence.exception.AuctionNotFoundException;
import com.ddang.ddang.configuration.IsolateDatabase;
import com.ddang.ddang.review.application.dto.response.ReadSingleReviewDto;
import com.ddang.ddang.review.application.dto.response.ReadMultipleReviewDto;
import com.ddang.ddang.review.application.exception.AlreadyReviewException;
import com.ddang.ddang.review.application.exception.InvalidUserToReview;
import com.ddang.ddang.review.infrastructure.exception.ReviewNotFoundException;
import com.ddang.ddang.review.application.exception.RevieweeNotFoundException;
import com.ddang.ddang.review.application.exception.ReviewerNotFoundException;
import com.ddang.ddang.review.application.fixture.ReviewServiceFixture;
import java.util.List;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@IsolateDatabase
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
class ReviewServiceTest extends ReviewServiceFixture {

    @Autowired
    ReviewService reviewService;

    @Test
    void 평가를_등록한다() {
        // when
        final Long actual = reviewService.create(구매자에_대한_평가_등록을_위한_DTO);

        // then
        assertThat(actual).isPositive();
    }

    @Test
    void 연관된_경매를_찾을_수_없다면_예외가_발생한다() {
        // when & then
        assertThatThrownBy(() -> reviewService.create(존재하지_않는_경매와_연관된_평가를_등록하려는_DTO))
                .isInstanceOf(AuctionNotFoundException.class);
    }

    @Test
    void 리뷰어를_찾을_수_없다면_예외가_발생한다() {
        // when & then
        assertThatThrownBy(() -> reviewService.create(존재하지_않는_사용자가_평가를_등록하려는_DTO))
                .isInstanceOf(ReviewerNotFoundException.class);
    }

    @Test
    void 리뷰이를_찾을_수_없다면_예외가_발생한다() {
        // when & then
        assertThatThrownBy(() -> reviewService.create(존재하지_않는_사용자를_평가하려는_DTO))
                .isInstanceOf(RevieweeNotFoundException.class);
    }

    @Test
    void 경매의_판매자나_낙찰자가_아닌_사용자일_경우_예외가_발생한다() {
        // when & then
        assertThatThrownBy(() -> reviewService.create(경매_참여자가_아닌_사용자가_평가를_등록하려는_DTO))
                .isInstanceOf(InvalidUserToReview.class)
                .hasMessage("경매의 판매자 또는 최종 낙찰자만 평가가 가능합니다.");
    }

    @Test
    void 이미_평가했는데_평가를_등록한다면_예외가_발생한다() {
        // when & then
        assertThatThrownBy(() -> reviewService.create(이미_평가한_경매와_연관된_평가를_또_등록하려는_DTO))
                .isInstanceOf(AlreadyReviewException.class)
                .hasMessage("이미 평가하였습니다.");
    }

    @Test
    void 지정한_평가_아이디에_해당하는_평가를_조회한다() {
        // when
        final ReadSingleReviewDto actual = reviewService.readByReviewId(구매자가_판매자1에게_받은_평가.getId());

        // then
        SoftAssertions.assertSoftly(softAssertions -> {
            softAssertions.assertThat(actual.score()).isEqualTo(구매자가_판매자1에게_받은_평가.getScore().getValue());
            softAssertions.assertThat(actual.content()).isEqualTo(구매자가_판매자1에게_받은_평가.getContent());
        });
    }

    @Test
    void 지정한_평가_아이디에_해당하는_평가가_없으면_예외가_발생한다() {
        // when & then
        assertThatThrownBy(() -> reviewService.readByReviewId(존재하지_않는_평가_아이디))
                .isInstanceOf(ReviewNotFoundException.class);
    }

    @Test
    void 지정한_아이디가_평가_대상인_평가_목록을_최신순으로_조회한다() {
        // when
        final List<ReadMultipleReviewDto> actual = reviewService.readAllByTargetId(구매자.getId());

        // then
        SoftAssertions.assertSoftly(softAssertions -> {
            softAssertions.assertThat(actual).hasSize(2);
            softAssertions.assertThat(actual.get(0).id()).isEqualTo(구매자가_판매자2에게_받은_평가.getId());
            softAssertions.assertThat(actual.get(0).reviewer().id()).isEqualTo(판매자2.getId());
            softAssertions.assertThat(actual.get(0).content()).isEqualTo(구매자가_판매자2에게_받은_평가.getContent());
            softAssertions.assertThat(actual.get(0).score()).isEqualTo(구매자가_판매자2에게_받은_평가.getScore().getValue());
            softAssertions.assertThat(actual.get(1).id()).isEqualTo(구매자가_판매자1에게_받은_평가.getId());
            softAssertions.assertThat(actual.get(1).reviewer().id()).isEqualTo(판매자1.getId());
            softAssertions.assertThat(actual.get(1).content()).isEqualTo(구매자가_판매자1에게_받은_평가.getContent());
            softAssertions.assertThat(actual.get(1).score()).isEqualTo(구매자가_판매자1에게_받은_평가.getScore().getValue());
        });
    }

    @Test
    void 지정한_경매_아이디와_작성자_아이디가_해당하는_평가가_존재한다면_dto에_넣어_반환한다() {
        // when
        final ReadSingleReviewDto actual = reviewService.readByAuctionIdAndWriterId(판매자1.getId(), 판매자1이_평가한_경매.getId());

        // then
        SoftAssertions.assertSoftly(softAssertions -> {
            softAssertions.assertThat(actual.score()).isEqualTo(구매자가_판매자1에게_받은_평가.getScore().getValue());
            softAssertions.assertThat(actual.content()).isEqualTo(구매자가_판매자1에게_받은_평가.getContent());
        });
    }

    @Test
    void 지정한_경매_아이디와_작성자_아이디가_해당하는_평가가_존재하지_않는다면_dto의_필드가_null이다() {
        // when
        final ReadSingleReviewDto actual = reviewService.readByAuctionIdAndWriterId(평가_안한_경매_판매자.getId(), 평가_안한_경매.getId());

        // then
        SoftAssertions.assertSoftly(softAssertions -> {
            softAssertions.assertThat(actual.score()).isNull();
            softAssertions.assertThat(actual.content()).isNull();
        });
    }
}
