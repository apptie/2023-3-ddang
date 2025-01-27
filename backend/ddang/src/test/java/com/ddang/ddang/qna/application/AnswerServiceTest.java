package com.ddang.ddang.qna.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ddang.ddang.auction.application.exception.UserForbiddenException;
import com.ddang.ddang.configuration.IsolateDatabase;
import com.ddang.ddang.qna.application.event.AnswerNotificationEvent;
import com.ddang.ddang.qna.application.exception.AlreadyAnsweredException;
import com.ddang.ddang.qna.infrastructure.exception.AnswerNotFoundException;
import com.ddang.ddang.qna.application.exception.InvalidAnswererException;
import com.ddang.ddang.qna.infrastructure.exception.QuestionNotFoundException;
import com.ddang.ddang.qna.application.fixture.AnswerServiceFixture;
import com.ddang.ddang.user.infrastructure.exception.UserNotFoundException;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.context.event.RecordApplicationEvents;

@IsolateDatabase
@RecordApplicationEvents
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
class AnswerServiceTest extends AnswerServiceFixture {

    @Autowired
    AnswerService answerService;

    @Autowired
    ApplicationEvents events;

    @Test
    void 답변을_등록한다() {
        // when
        final Long actual = answerService.create(답변_등록_요청_dto, 이미지_절대_경로);

        // then
        assertThat(actual).isPositive();
    }

    @Test
    void 답변을_삭제한_질문에_다시_답변을_달_수_있다() {
        // when
        final Long actual = answerService.create(답변이_삭제된_질문에_답변_등록_요청_dto, 이미지_절대_경로);

        // then
        assertThat(actual).isPositive();
    }

    @Test
    void 존재하지_않는_사용자가_질문에_답하는_경우_예외가_발생한다() {
        // when & then
        assertThatThrownBy(() -> answerService.create(존재하지_않는_사용자의_답변_등록_요청_dto, 이미지_절대_경로))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void 존재하지_않는_질문에_답하는_경우_예외가_발생한다() {
        // when & then
        assertThatThrownBy(() -> answerService.create(존재하지_않는_질문에_답변_등록_요청_dto, 이미지_절대_경로))
                .isInstanceOf(QuestionNotFoundException.class);
    }

    @Test
    void 판매자가_아닌_다른_사용자가_질문에_답하는_경우_예외가_발생한다() {
        // when & then
        assertThatThrownBy(() -> answerService.create(판매자가_아닌_사용자가_질문에_답변_등록_요청_dto, 이미지_절대_경로))
                .isInstanceOf(InvalidAnswererException.class)
                .hasMessage("판매자만 답변할 수 있습니다.");
    }

    @Test
    void 이미_답변한_질문에_답하는_경우_예외가_발생한다() {
        // when & then
        assertThatThrownBy(() -> answerService.create(이미_답변한_질문에_답변_등록_요청_dto, 이미지_절대_경로))
                .isInstanceOf(AlreadyAnsweredException.class)
                .hasMessage("이미 답변한 질문입니다.");
    }

    @Test
    void 답변을_삭제한다() {
        // when
        answerService.deleteById(답변.getId(), 판매자.getId());

        // then
        SoftAssertions.assertSoftly(softAssertions -> {
            softAssertions.assertThat(답변.isDeleted()).isTrue();
            softAssertions.assertThat(답변.getQuestion()).isNull();
            softAssertions.assertThat(답변한_질문.getAnswer()).isNull();
        });
    }

    @Test
    void 존재하지_않는_답변_삭제시_예외가_발생한다() {
        // when & then
        assertThatThrownBy(() -> answerService.deleteById(존재하지_않는_답변_아이디, 판매자.getId()))
                .isInstanceOf(AnswerNotFoundException.class);
    }

    @Test
    void 존재하지_않는_사용자가_답변_삭제시_예외가_발생한다() {
        // when & then
        assertThatThrownBy(() -> answerService.deleteById(답변.getId(), 존재하지_않는_사용자_아이디))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void 답변_작성자가_아닌_사용자가_답변_삭제시_예외가_발생한다() {
        // when & then
        assertThatThrownBy(() -> answerService.deleteById(답변.getId(), 판매자가_아닌_사용자.getId()))
                .isInstanceOf(UserForbiddenException.class)
                .hasMessage("권한이 없습니다.");
    }

    @Test
    void 질문에_대한_답변이_생성되면_질문자에게_알림을_보낸다() {
        // when
        answerService.create(답변_등록_요청_dto, 이미지_절대_경로);
        final long actual = events.stream(AnswerNotificationEvent.class).count();

        // then
        assertThat(actual).isEqualTo(1);
    }
}
