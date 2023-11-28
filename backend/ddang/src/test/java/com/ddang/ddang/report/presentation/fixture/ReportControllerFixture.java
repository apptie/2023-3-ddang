package com.ddang.ddang.report.presentation.fixture;

import com.ddang.ddang.authentication.infrastructure.jwt.PrivateClaims;
import com.ddang.ddang.configuration.CommonControllerSliceTest;
import com.ddang.ddang.report.application.dto.response.ReadAnswerReportDto;
import com.ddang.ddang.report.application.dto.response.ReadAnswerReportDto.ReportedAnswerInfoDto;
import com.ddang.ddang.report.application.dto.response.ReadAnswerReportDto.ReportedAnswerInfoDto.AnswererInfoDto;
import com.ddang.ddang.report.application.dto.response.ReadAuctionReportDto;
import com.ddang.ddang.report.application.dto.response.ReadAuctionReportDto.ReportedAuctionInfoDto;
import com.ddang.ddang.report.application.dto.response.ReadChatRoomReportDto;
import com.ddang.ddang.report.application.dto.response.ReadChatRoomReportDto.ReportedChatRoomInfoDto;
import com.ddang.ddang.report.application.dto.response.ReadChatRoomReportDto.ReportedChatRoomInfoDto.PartnerInfoDto;
import com.ddang.ddang.report.application.dto.response.ReadChatRoomReportDto.ReportedChatRoomInfoDto.SimpleAuctionInfoDto;
import com.ddang.ddang.report.application.dto.response.ReadChatRoomReportDto.ReportedChatRoomInfoDto.SimpleAuctionInfoDto.SellerInfoDto;
import com.ddang.ddang.report.application.dto.response.ReadQuestionReportDto;
import com.ddang.ddang.report.application.dto.response.ReadQuestionReportDto.ReportedQuestionInfoDto;
import com.ddang.ddang.report.application.dto.response.ReadQuestionReportDto.ReportedQuestionInfoDto.QuestionerInfoDto;
import com.ddang.ddang.report.application.dto.response.ReadQuestionReportDto.ReporterInfoDto;
import com.ddang.ddang.report.presentation.dto.request.CreateAnswerReportRequest;
import com.ddang.ddang.report.presentation.dto.request.CreateAuctionReportRequest;
import com.ddang.ddang.report.presentation.dto.request.CreateChatRoomReportRequest;
import com.ddang.ddang.report.presentation.dto.request.CreateQuestionReportRequest;

import java.time.LocalDateTime;

@SuppressWarnings("NonAsciiCharacters")
public class ReportControllerFixture extends CommonControllerSliceTest {

    protected Long 생성된_경매_신고_아이디 = 1L;
    protected Long 생성된_채팅방_신고_아이디 = 1L;
    protected Long 생성된_질문_신고_아이디 = 1L;
    protected Long 생성된_답변_신고_아이디 = 1L;
    protected String 엑세스_토큰_값 = "Bearer accessToken";
    protected PrivateClaims 사용자_ID_클레임 = new PrivateClaims(1L);
    protected PrivateClaims 존재하지_않는_사용자_ID_클레임 = new PrivateClaims(-999L);
    protected PrivateClaims 채팅방_참여자가_아닌_사용자_ID_클레임 = new PrivateClaims(999L);
    protected CreateAuctionReportRequest 경매_신고_request = new CreateAuctionReportRequest(1L, "신고합니다");
    protected CreateAuctionReportRequest 경매_아이디가_없는_신고_request = new CreateAuctionReportRequest(null, "신고합니다");
    protected CreateAuctionReportRequest 경매_아이디가_음수인_신고_request = new CreateAuctionReportRequest(-999L, "신고합니다");
    protected static CreateAuctionReportRequest 신고_내용이_null인_경매_신고_request = new CreateAuctionReportRequest(1L, null);
    protected static CreateAuctionReportRequest 신고_내용이_빈값인_경매_신고_request = new CreateAuctionReportRequest(1L, "");
    private ReportedAuctionInfoDto 신고할_경매_정보_dto = new ReportedAuctionInfoDto(
            1L,
            new ReportedAuctionInfoDto.SellerInfoDto(1L, "판매자", "store-name.png", 4.0d, "12345", false),
            "제목",
            "설명",
            100,
            1_000,
            false,
            LocalDateTime.now().plusDays(2),
            2
    );
    protected ReadAuctionReportDto 경매_신고_dto1 = new ReadAuctionReportDto(
            1L,
            new ReadAuctionReportDto.ReporterInfoDto(2L, "회원1", "store-name.png", 5.0, false),
            LocalDateTime.now(),
            new ReportedAuctionInfoDto(
                    1L,
                    new ReportedAuctionInfoDto.SellerInfoDto(1L, "판매자", "store-name.png", 4.0d, "12345", false),
                    "제목",
                    "설명",
                    100,
                    1_000,
                    false,
                    LocalDateTime.now().plusDays(2),
                    2
            ),
            "신고합니다."
    );
    protected ReadAuctionReportDto 경매_신고_dto2 = new ReadAuctionReportDto(
            2L,
            new ReadAuctionReportDto.ReporterInfoDto(3L, "회원2", "store-name.png", 5.0, false),
            LocalDateTime.now(),
            신고할_경매_정보_dto,
            "신고합니다."
    );
    protected ReadAuctionReportDto 경매_신고_dto3 = new ReadAuctionReportDto(
            3L,
            new ReadAuctionReportDto.ReporterInfoDto(4L, "회원3", "store-name.png", 5.0, false),
            LocalDateTime.now(),
            신고할_경매_정보_dto,
            "신고합니다."
    );

    protected CreateChatRoomReportRequest 채팅방_신고_request = new CreateChatRoomReportRequest(1L, "신고합니다");
    protected CreateChatRoomReportRequest 존재하지_않는_채팅방_신고_request = new CreateChatRoomReportRequest(9999L, "신고합니다");
    protected CreateChatRoomReportRequest 채팅방_아이디가_null인_신고_request = new CreateChatRoomReportRequest(null, "신고합니다");
    protected CreateChatRoomReportRequest 채팅방_아이디가_음수인_신고_request = new CreateChatRoomReportRequest(-999L, "신고합니다");
    protected static CreateChatRoomReportRequest 신고_내용이_null인_채팅_신고_request = new CreateChatRoomReportRequest(1L, null);
    protected static CreateChatRoomReportRequest 신고_내용이_빈값인_채팅_신고_request = new CreateChatRoomReportRequest(-999L, "");
    protected ReadChatRoomReportDto 채팅방_신고_dto1 = new ReadChatRoomReportDto(
            2L,
            new ReadChatRoomReportDto.ReporterInfoDto(2L, "구매자1", "store-name.png", 4.0d, false),
            LocalDateTime.now(),
            new ReportedChatRoomInfoDto(
                    1L,
                    new SimpleAuctionInfoDto(
                            1L,
                            new SellerInfoDto(
                                    1L, "판매자", "store-name.png", 4.0d, "12345", false
                            ),
                            "제목",
                            "설명",
                            100,
                            1_00,
                            false,
                            LocalDateTime.now().plusDays(2),
                            2
                    ),
                    new PartnerInfoDto(2L, "구매자1", "store-name.png", 4.0d, "12346", false)
            ),
            "신고합니다."
    );

    protected ReadChatRoomReportDto 채팅방_신고_dto2 = new ReadChatRoomReportDto(
            2L,
            new ReadChatRoomReportDto.ReporterInfoDto(3L, "구매자2", "store-name.png", 4.0d, false),
            LocalDateTime.now(),
            new ReportedChatRoomInfoDto(
                    1L,
                    new SimpleAuctionInfoDto(
                            2L,
                            new SellerInfoDto(
                                    1L, "판매자", "store-name.png", 4.0d, "12345", false
                            ),
                            "제목",
                            "설명",
                            100,
                            1_00,
                            false,
                            LocalDateTime.now().plusDays(2),
                            2
                    ),
                    new PartnerInfoDto(3L, "구매자2", "store-name.png", 4.0d, "12347", false)
            ),
            "신고합니다."
    );
    protected ReadChatRoomReportDto 채팅방_신고_dto3 = new ReadChatRoomReportDto(
            3L,
            new ReadChatRoomReportDto.ReporterInfoDto(3L, "구매자2", "store-name.png", 4.0d, false),
            LocalDateTime.now(),
            new ReportedChatRoomInfoDto(
                    1L,
                    new SimpleAuctionInfoDto(
                            3L,
                            new SellerInfoDto(1L, "판매자", "store-name.png", 4.0d, "12345", false),
                            "제목",
                            "설명",
                            100,
                            1_00,
                            false,
                            LocalDateTime.now().plusDays(2),
                            2
                    ),
                    new PartnerInfoDto(3L, "구매자2", "store-name.png", 4.0d, "12347", false)
            ),
            "신고합니다."
    );

    protected CreateQuestionReportRequest 질문_신고_request = new CreateQuestionReportRequest(1L, 1L, "신고합니다.");
    protected CreateQuestionReportRequest 존재하지_않는_질문_신고_request = new CreateQuestionReportRequest(1L, 999L, "신고합니다.");
    protected CreateQuestionReportRequest 본인의_질문_신고_request = new CreateQuestionReportRequest(1L, 1L, "신고합니다.");
    protected CreateQuestionReportRequest 이미_신고한_질문_신고_request = new CreateQuestionReportRequest(1L, 1L, "신고합니다.");
    protected CreateQuestionReportRequest 경매_아이디가_null인_질문_신고_request = new CreateQuestionReportRequest(null, 1L, "신고합니다.");
    protected CreateQuestionReportRequest 경매_아이디가_음수인_질문_신고_request = new CreateQuestionReportRequest(-1L, 1L, "신고합니다.");
    protected CreateQuestionReportRequest 질문_아이디가_null인_질문_신고_request = new CreateQuestionReportRequest(1L, null, "신고합니다.");
    protected CreateQuestionReportRequest 질문_아이디가_음수인_질문_신고_request = new CreateQuestionReportRequest(1L, -1L, "신고합니다.");
    protected static CreateQuestionReportRequest 신고_내용이_null인_질문_신고_request = new CreateQuestionReportRequest(1L, 1L, null);
    protected static CreateQuestionReportRequest 신고_내용이_빈값인_질문_신고_request = new CreateQuestionReportRequest(1L, 1L, "");
    protected ReadQuestionReportDto 질문_신고_dto1 = new ReadQuestionReportDto(
            1L,
            new ReporterInfoDto(2L, "구매자1", "store-name.png", 4.0d, false),
            LocalDateTime.now(),
            new ReportedQuestionInfoDto(
                    1L,
                    new QuestionerInfoDto(1L, "사용자", "store-name.png", 5.0d, "12345", false),
                    "질문드립니다.",
                    LocalDateTime.now()
            ),
            "신고합니다."
    );
    protected ReadQuestionReportDto 질문_신고_dto2 = new ReadQuestionReportDto(
            2L,
            new ReporterInfoDto(2L, "구매자1", "store-name.png", 4.0d, false),
            LocalDateTime.now(),
            new ReportedQuestionInfoDto(
                    2L,
                    new QuestionerInfoDto(1L, "사용자", "store-name.png", 5.0d, "12345", false),
                    "질문드립니다.",
                    LocalDateTime.now()
            ),
            "신고합니다.");
    protected ReadQuestionReportDto 질문_신고_dto3 = new ReadQuestionReportDto(
            3L,
            new ReporterInfoDto(2L, "구매자1", "store-name.png", 4.0d, false),
            LocalDateTime.now(),
            new ReportedQuestionInfoDto(
                    3L,
                    new QuestionerInfoDto(1L, "사용자", "store-name.png", 5.0d, "12345", false),
                    "질문드립니다.",
                    LocalDateTime.now()
            ),
            "신고합니다."
    );

    protected CreateAnswerReportRequest 답변_신고_request = new CreateAnswerReportRequest(1L, 1L, "신고합니다.");
    protected CreateAnswerReportRequest 존재하지_않는_답변_신고_request = new CreateAnswerReportRequest(1L, 999L, "신고합니다.");
    protected CreateAnswerReportRequest 본인의_답변_신고_request = new CreateAnswerReportRequest(1L, 1L, "신고합니다.");
    protected CreateAnswerReportRequest 이미_신고한_답변_신고_request = new CreateAnswerReportRequest(1L, 1L, "신고합니다.");
    protected CreateAnswerReportRequest 경매_아이디가_null인_답변_신고_request = new CreateAnswerReportRequest(null, 1L, "신고합니다.");
    protected CreateAnswerReportRequest 경매_아이디가_음수인_답변_신고_request = new CreateAnswerReportRequest(-1L, 1L, "신고합니다.");
    protected CreateAnswerReportRequest 질문_아이디가_null인_답변_신고_request = new CreateAnswerReportRequest(1L, null, "신고합니다.");
    protected CreateAnswerReportRequest 답변_아이디가_음수인_질문_신고_request = new CreateAnswerReportRequest(1L, -1L, "신고합니다.");
    protected static CreateAnswerReportRequest 신고_내용이_null인_답변_신고_request = new CreateAnswerReportRequest(1L, 1L, null);
    protected static CreateAnswerReportRequest 신고_내용이_빈값인_답변_신고_request = new CreateAnswerReportRequest(1L, 1L, "");
    protected ReadAnswerReportDto 답변_신고_dto1 = new ReadAnswerReportDto(
            1L,
            new ReadAnswerReportDto.ReporterInfoDto(2L, "구매자1", "store-name.png", 4.0d, false),
            LocalDateTime.now(),
            new ReportedAnswerInfoDto(
                    1L,
                    new AnswererInfoDto(1L, "사용자", "store-name.png", 5.0d, "12345", false),
                    "답변드립니다.",
                    LocalDateTime.now()
            ),
            "신고합니다."
    );
    protected ReadAnswerReportDto 답변_신고_dto2 = new ReadAnswerReportDto(
            2L,
            new ReadAnswerReportDto.ReporterInfoDto(2L, "구매자1", "store-name.png", 4.0d, false),
            LocalDateTime.now(),
            new ReportedAnswerInfoDto(
                    2L,
                    new AnswererInfoDto(1L, "사용자", "store-name.png", 5.0d, "12345", false),
                    "답변드립니다.",
                    LocalDateTime.now()
            ),
            "신고합니다."
    );
    protected ReadAnswerReportDto 답변_신고_dto3 = new ReadAnswerReportDto(
            3L,
            new ReadAnswerReportDto.ReporterInfoDto(2L, "구매자1", "store-name.png", 4.0d, false),
            LocalDateTime.now(),
            new ReportedAnswerInfoDto(
                    3L,
                    new AnswererInfoDto(1L, "사용자", "store-name.png", 5.0d, "12345", false),
                    "답변드립니다.",
                    LocalDateTime.now()
            ),
            "신고합니다."
    );
}
