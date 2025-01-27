package com.ddang.ddang.report.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ddang.ddang.chat.infrastructure.exception.ChatRoomNotFoundException;
import com.ddang.ddang.configuration.IsolateDatabase;
import com.ddang.ddang.report.application.dto.response.ReadChatRoomReportDto;
import com.ddang.ddang.report.application.exception.AlreadyReportChatRoomException;
import com.ddang.ddang.report.application.exception.InvalidChatRoomReportException;
import com.ddang.ddang.report.application.fixture.ChatRoomReportServiceFixture;
import com.ddang.ddang.user.infrastructure.exception.UserNotFoundException;
import java.util.List;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@IsolateDatabase
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
class ChatRoomReportServiceTest extends ChatRoomReportServiceFixture {

    @Autowired
    ChatRoomReportService chatRoomReportService;

    @Test
    void 채팅방_신고를_등록한다() {
        // when
        final Long actual = chatRoomReportService.create(채팅방_신고_요청_dto);

        // then
        assertThat(actual).isPositive();
    }

    @Test
    void 존재하지_않는_사용자가_채팅방을_신고할시_예외가_발생한다() {
        // when & then
        assertThatThrownBy(() -> chatRoomReportService.create(존재하지_않는_사용자의_채팅방_신고_요청_dto))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void 존재하지_않는_채팅방을_신고할시_예외가_발생한다() {
        // when & then
        assertThatThrownBy(() -> chatRoomReportService.create(존재하지_않는_채팅방_신고_요청_dto))
                .isInstanceOf(ChatRoomNotFoundException.class);
    }

    @Test
    void 판매자와_구매자_외의_사용자가_채팅방을_신고할시_예외가_발생한다() {
        // when & then
        assertThatThrownBy(() -> chatRoomReportService.create(참여자가_아닌_사용자의_채팅방_신고_요청_dto))
                .isInstanceOf(InvalidChatRoomReportException.class)
                .hasMessage("해당 채팅방을 신고할 권한이 없습니다.");
    }

    @Test
    void 이미_신고한_채팅방을_동일_사용자가_신고하는_경우_예외가_발생한다() {
        // when & then
        assertThatThrownBy(() -> chatRoomReportService.create(이미_신고한_사용자의_채팅방_신고_요청_dto))
                .isInstanceOf(AlreadyReportChatRoomException.class)
                .hasMessage("이미 신고한 채팅방입니다.");
    }

    @Test
    void 전체_신고_목록을_조회한다() {
        // when
        final List<ReadChatRoomReportDto> actual = chatRoomReportService.readAll();

        // then
        SoftAssertions.assertSoftly(softAssertions -> {
            softAssertions.assertThat(actual).hasSize(3);
            softAssertions.assertThat(actual.get(0).reporterDto().id()).isEqualTo(이미_신고한_구매자1.getId());
            softAssertions.assertThat(actual.get(0).chatRoomDto().id()).isEqualTo(채팅방1.getId());
            softAssertions.assertThat(actual.get(1).reporterDto().id()).isEqualTo(이미_신고한_구매자2.getId());
            softAssertions.assertThat(actual.get(1).chatRoomDto().id()).isEqualTo(채팅방2.getId());
            softAssertions.assertThat(actual.get(2).reporterDto().id()).isEqualTo(이미_신고한_구매자3.getId());
            softAssertions.assertThat(actual.get(2).chatRoomDto().id()).isEqualTo(채팅방3.getId());
        });
    }
}
