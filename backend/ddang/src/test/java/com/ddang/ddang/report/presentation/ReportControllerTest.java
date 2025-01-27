package com.ddang.ddang.report.presentation;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ddang.ddang.auction.infrastructure.persistence.exception.AuctionNotFoundException;
import com.ddang.ddang.authentication.configuration.AuthenticationInterceptor;
import com.ddang.ddang.authentication.configuration.AuthenticationPrincipalArgumentResolver;
import com.ddang.ddang.authentication.configuration.AuthenticationStore;
import com.ddang.ddang.authentication.domain.TokenDecoder;
import com.ddang.ddang.authentication.domain.TokenType;
import com.ddang.ddang.chat.infrastructure.exception.ChatRoomNotFoundException;
import com.ddang.ddang.common.exception.GlobalExceptionHandler;
import com.ddang.ddang.qna.application.exception.InvalidAnswererException;
import com.ddang.ddang.qna.infrastructure.exception.AnswerNotFoundException;
import com.ddang.ddang.qna.infrastructure.exception.QuestionNotFoundException;
import com.ddang.ddang.report.application.dto.request.CreateAnswerReportDto;
import com.ddang.ddang.report.application.dto.request.CreateAuctionReportDto;
import com.ddang.ddang.report.application.dto.request.CreateChatRoomReportDto;
import com.ddang.ddang.report.application.dto.request.CreateQuestionReportDto;
import com.ddang.ddang.report.application.exception.AlreadyReportAuctionException;
import com.ddang.ddang.report.application.exception.AlreadyReportChatRoomException;
import com.ddang.ddang.report.application.exception.InvalidChatRoomReportException;
import com.ddang.ddang.report.application.exception.InvalidQuestionReportException;
import com.ddang.ddang.report.application.exception.InvalidReportAuctionException;
import com.ddang.ddang.report.application.exception.InvalidReporterToAuctionException;
import com.ddang.ddang.report.presentation.dto.request.CreateAnswerReportRequest;
import com.ddang.ddang.report.presentation.dto.request.CreateAuctionReportRequest;
import com.ddang.ddang.report.presentation.dto.request.CreateChatRoomReportRequest;
import com.ddang.ddang.report.presentation.dto.request.CreateQuestionReportRequest;
import com.ddang.ddang.report.presentation.fixture.ReportControllerFixture;
import com.ddang.ddang.user.infrastructure.exception.UserNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@SuppressWarnings("NonAsciiCharacters")
class ReportControllerTest extends ReportControllerFixture {

    TokenDecoder tokenDecoder;

    MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        tokenDecoder = mock(TokenDecoder.class);

        final AuthenticationStore store = new AuthenticationStore();
        final AuthenticationInterceptor interceptor = new AuthenticationInterceptor(
                blackListTokenService,
                authenticationUserService,
                tokenDecoder,
                store
        );
        final AuthenticationPrincipalArgumentResolver resolver = new AuthenticationPrincipalArgumentResolver(store);

        mockMvc = MockMvcBuilders.standaloneSetup(reportController)
                                 .setControllerAdvice(new GlobalExceptionHandler())
                                 .addInterceptors(interceptor)
                                 .setCustomArgumentResolvers(resolver)
                                 .setMessageConverters(mappingJackson2HttpMessageConverter)
                                 .apply(MockMvcRestDocumentation.documentationConfiguration(provider))
                                 .alwaysDo(print())
                                 .alwaysDo(restDocs)
                                 .build();
    }

    @Test
    void 경매_신고를_등록한다() throws Exception {
        // given
        given(tokenDecoder.decode(eq(TokenType.ACCESS), anyString())).willReturn(Optional.of(사용자_ID_클레임));
        given(auctionReportService.create(any(CreateAuctionReportDto.class))).willReturn(생성된_경매_신고_아이디);

        // when & then
        final ResultActions resultActions = mockMvc.perform(post("/reports/auctions")
                                                           .header(HttpHeaders.AUTHORIZATION, 엑세스_토큰_값)
                                                           .contentType(MediaType.APPLICATION_JSON)
                                                           .content(objectMapper.writeValueAsString(경매_신고_request))
                                                   )
                                                   .andExpectAll(
                                                           status().isCreated(),
                                                           header().string(HttpHeaders.LOCATION, is("/auctions/1"))
                                                   );

        createAuctionReport_문서화(resultActions);
    }

    @Test
    void 해당_사용자가_없는_경우_신고시_404를_반환한다() throws Exception {
        // given
        given(tokenDecoder.decode(eq(TokenType.ACCESS), anyString())).willReturn(Optional.of(존재하지_않는_사용자_ID_클레임));
        given(auctionReportService.create(any(CreateAuctionReportDto.class)))
                .willThrow(new UserNotFoundException("해당 사용자를 찾을 수 없습니다."));

        // when & then
        mockMvc.perform(post("/reports/auctions")
                       .header(HttpHeaders.AUTHORIZATION, 엑세스_토큰_값)
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(경매_신고_request))
               )
               .andExpectAll(
                       status().isNotFound(),
                       jsonPath("$.message").exists()
               );
    }

    @Test
    void 해당_경매가_없는_경우_신고시_404를_반환한다() throws Exception {
        // given
        given(tokenDecoder.decode(eq(TokenType.ACCESS), anyString())).willReturn(Optional.of(사용자_ID_클레임));
        given(auctionReportService.create(any(CreateAuctionReportDto.class)))
                .willThrow(new AuctionNotFoundException("해당 경매를 찾을 수 없습니다."));

        // when & then
        mockMvc.perform(post("/reports/auctions")
                       .header(HttpHeaders.AUTHORIZATION, 엑세스_토큰_값)
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(경매_신고_request))
               )
               .andExpectAll(
                       status().isNotFound(),
                       jsonPath("$.message").exists()
               );
    }

    @Test
    void 본인이_등록한_경매를_신고할_경우_400을_반환한다() throws Exception {
        // given
        given(tokenDecoder.decode(eq(TokenType.ACCESS), anyString())).willReturn(Optional.of(사용자_ID_클레임));
        given(auctionReportService.create(any(CreateAuctionReportDto.class)))
                .willThrow(new InvalidReporterToAuctionException("본인 경매글입니다."));

        // when & then
        mockMvc.perform(post("/reports/auctions")
                       .header(HttpHeaders.AUTHORIZATION, 엑세스_토큰_값)
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(경매_신고_request))
               )
               .andExpectAll(
                       status().isBadRequest(),
                       jsonPath("$.message").exists()
               );
    }

    @Test
    void 이미_삭제된_경매_신고시_400을_반환한다() throws Exception {
        // given
        given(tokenDecoder.decode(eq(TokenType.ACCESS), anyString())).willReturn(Optional.of(사용자_ID_클레임));
        given(auctionReportService.create(any(CreateAuctionReportDto.class)))
                .willThrow(new InvalidReportAuctionException("이미 삭제된 경매입니다."));

        // when & then
        mockMvc.perform(post("/reports/auctions")
                       .header(HttpHeaders.AUTHORIZATION, 엑세스_토큰_값)
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(경매_신고_request))
               )
               .andExpectAll(
                       status().isBadRequest(),
                       jsonPath("$.message").exists()
               );
    }

    @Test
    void 이미_신고한_경매_신고시_400을_반환한다() throws Exception {
        // given
        given(tokenDecoder.decode(eq(TokenType.ACCESS), anyString())).willReturn(Optional.of(사용자_ID_클레임));
        given(auctionReportService.create(any(CreateAuctionReportDto.class)))
                .willThrow(new AlreadyReportAuctionException("이미 신고한 경매입니다."));

        // when & then
        mockMvc.perform(post("/reports/auctions")
                       .header(HttpHeaders.AUTHORIZATION, 엑세스_토큰_값)
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(경매_신고_request))
               )
               .andExpectAll(
                       status().isBadRequest(),
                       jsonPath("$.message").exists()
               );
    }

    @Test
    void 경매_아이디가_없는_경우_신고시_400을_반환한다() throws Exception {
        // given
        given(tokenDecoder.decode(eq(TokenType.ACCESS), anyString())).willReturn(Optional.of(사용자_ID_클레임));

        // when & then
        mockMvc.perform(post("/reports/auctions")
                       .header(HttpHeaders.AUTHORIZATION, 엑세스_토큰_값)
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(경매_아이디가_없는_신고_request))
               )
               .andExpectAll(
                       status().isBadRequest(),
                       jsonPath("$.message").exists()
               );
    }

    @Test
    void 경매_아이디가_음수인_경우_신고시_400을_반환한다() throws Exception {
        // given
        given(tokenDecoder.decode(eq(TokenType.ACCESS), anyString())).willReturn(Optional.of(사용자_ID_클레임));

        // when & then
        mockMvc.perform(post("/reports/auctions")
                       .header(HttpHeaders.AUTHORIZATION, 엑세스_토큰_값)
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(경매_아이디가_음수인_신고_request))
               )
               .andExpectAll(
                       status().isBadRequest(),
                       jsonPath("$.message").exists()
               );
    }

    @ParameterizedTest
    @MethodSource("provideAuctionReportRequestWithEmptyDescription")
    void 신고_내용_없이_경매_신고시_400을_반환한다(final CreateAuctionReportRequest 내용이_없는_경매_신고_요청) throws Exception {
        // given
        given(tokenDecoder.decode(eq(TokenType.ACCESS), anyString())).willReturn(Optional.of(사용자_ID_클레임));

        // when & then
        mockMvc.perform(post("/reports/auctions")
                       .header(HttpHeaders.AUTHORIZATION, 엑세스_토큰_값)
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(내용이_없는_경매_신고_요청))
               )
               .andExpectAll(
                       status().isBadRequest(),
                       jsonPath("$.message", is("신고 내용이 입력되지 않았습니다."))
               );
    }

    private static Stream<CreateAuctionReportRequest> provideAuctionReportRequestWithEmptyDescription() {
        return Stream.of(신고_내용이_null인_경매_신고_request, 신고_내용이_빈값인_경매_신고_request);
    }

    @Test
    void 전체_경매_신고_목록을_조회한다() throws Exception {
        // given
        given(tokenDecoder.decode(eq(TokenType.ACCESS), anyString())).willReturn(Optional.of(사용자_ID_클레임));
        given(auctionReportService.readAll()).willReturn(List.of(경매_신고_dto1, 경매_신고_dto2, 경매_신고_dto3));

        // when & then
        final ResultActions resultActions =
                mockMvc.perform(get("/reports/auctions")
                               .contentType(MediaType.APPLICATION_JSON)
                       )
                       .andExpectAll(
                               status().isOk(),
                               jsonPath("$.reports.[0].id", is(경매_신고_dto1.id()), Long.class),
                               jsonPath("$.reports.[0].reporter.id", is(경매_신고_dto1.reporterDto().id()), Long.class),
                               jsonPath("$.reports.[0].reporter.name", is(경매_신고_dto1.reporterDto().name())),
                               jsonPath("$.reports.[0].createdTime").exists(),
                               jsonPath("$.reports.[0].auction.id", is(경매_신고_dto1.auctionDto().id()), Long.class),
                               jsonPath("$.reports.[0].auction.title", is(경매_신고_dto1.auctionDto().title())),
                               jsonPath("$.reports.[0].description", is(경매_신고_dto1.description())),
                               jsonPath("$.reports.[1].id", is(경매_신고_dto2.id()), Long.class),
                               jsonPath("$.reports.[1].reporter.id", is(경매_신고_dto2.reporterDto().id()), Long.class),
                               jsonPath("$.reports.[1].reporter.name", is(경매_신고_dto2.reporterDto().name())),
                               jsonPath("$.reports.[1].createdTime").exists(),
                               jsonPath("$.reports.[1].auction.id", is(경매_신고_dto2.auctionDto().id()), Long.class),
                               jsonPath("$.reports.[1].auction.title", is(경매_신고_dto2.auctionDto().title())),
                               jsonPath("$.reports.[1].description", is(경매_신고_dto2.description())),
                               jsonPath("$.reports.[2].id", is(경매_신고_dto3.id()), Long.class),
                               jsonPath("$.reports.[2].reporter.id", is(경매_신고_dto3.reporterDto().id()), Long.class),
                               jsonPath("$.reports.[2].reporter.name", is(경매_신고_dto3.reporterDto().name())),
                               jsonPath("$.reports.[2].createdTime").exists(),
                               jsonPath("$.reports.[2].auction.id", is(경매_신고_dto3.auctionDto().id()), Long.class),
                               jsonPath("$.reports.[2].auction.title", is(경매_신고_dto3.auctionDto().title())),
                               jsonPath("$.reports.[2].description", is(경매_신고_dto3.description()))
                       );

        readAllAuctionReport_문서화(resultActions);
    }

    @Test
    void 채팅방_신고를_등록한다() throws Exception {
        // given
        given(tokenDecoder.decode(eq(TokenType.ACCESS), anyString())).willReturn(Optional.of(사용자_ID_클레임));
        given(chatRoomReportService.create(any(CreateChatRoomReportDto.class))).willReturn(생성된_채팅방_신고_아이디);

        // when & then
        final ResultActions resultActions = mockMvc.perform(post("/reports/chat-rooms")
                                                           .header(HttpHeaders.AUTHORIZATION, 엑세스_토큰_값)
                                                           .contentType(MediaType.APPLICATION_JSON)
                                                           .content(objectMapper.writeValueAsString(채팅방_신고_request))
                                                   )
                                                   .andExpectAll(
                                                           status().isCreated(),
                                                           header().string(HttpHeaders.LOCATION, is("/chattings/1"))
                                                   );

        createChatRoomReport_문서화(resultActions);
    }

    @Test
    void 존재하지_않은_사용자가_채팅방을_신고할시_404를_반환한다() throws Exception {
        // given
        given(tokenDecoder.decode(eq(TokenType.ACCESS), anyString())).willReturn(Optional.of(존재하지_않는_사용자_ID_클레임));
        given(chatRoomReportService.create(any(CreateChatRoomReportDto.class)))
                .willThrow(new UserNotFoundException("해당 사용자를 찾을 수 없습니다."));

        // when & then
        mockMvc.perform(post("/reports/chat-rooms")
                       .header(HttpHeaders.AUTHORIZATION, 엑세스_토큰_값)
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(채팅방_신고_request))
               )
               .andExpectAll(
                       status().isNotFound(),
                       jsonPath("$.message").exists()
               );
    }

    @Test
    void 존재하지_않은_채팅방을_신고할시_404를_반환한다() throws Exception {
        // given
        given(tokenDecoder.decode(eq(TokenType.ACCESS), anyString())).willReturn(Optional.of(사용자_ID_클레임));
        given(chatRoomReportService.create(any(CreateChatRoomReportDto.class)))
                .willThrow(new ChatRoomNotFoundException("해당 채팅방을 찾을 수 없습니다."));

        // when & then
        mockMvc.perform(post("/reports/chat-rooms")
                       .header(HttpHeaders.AUTHORIZATION, 엑세스_토큰_값)
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(존재하지_않는_채팅방_신고_request))
               )
               .andExpectAll(
                       status().isNotFound(),
                       jsonPath("$.message").exists()
               );
    }

    @Test
    void 판매자_혹은_구매자가_아닌_사용자가_채팅방을_신고할시_403을_반환한다() throws Exception {
        // given
        given(tokenDecoder.decode(eq(TokenType.ACCESS), anyString())).willReturn(Optional.of(채팅방_참여자가_아닌_사용자_ID_클레임));
        given(chatRoomReportService.create(any(CreateChatRoomReportDto.class)))
                .willThrow(new InvalidChatRoomReportException("해당 채팅방을 신고할 권한이 없습니다."));

        // when & then
        mockMvc.perform(post("/reports/chat-rooms")
                       .header(HttpHeaders.AUTHORIZATION, 엑세스_토큰_값)
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(채팅방_신고_request))
               )
               .andExpectAll(
                       status().isForbidden(),
                       jsonPath("$.message").exists()
               );
    }

    @Test
    void 이미_신고한_사용자가_동일_채팅방을_신고할시_400을_반환한다() throws Exception {
        // given
        given(tokenDecoder.decode(eq(TokenType.ACCESS), anyString())).willReturn(Optional.of(사용자_ID_클레임));
        given(chatRoomReportService.create(any(CreateChatRoomReportDto.class)))
                .willThrow(new AlreadyReportChatRoomException("이미 신고한 채팅방입니다."));

        // when & then
        mockMvc.perform(post("/reports/chat-rooms")
                       .header(HttpHeaders.AUTHORIZATION, 엑세스_토큰_값)
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(채팅방_신고_request))
               )
               .andExpectAll(
                       status().isBadRequest(),
                       jsonPath("$.message").exists()
               );
    }

    @Test
    void 채팅방_아이디가_없는_경우_신고시_400을_반환한다() throws Exception {
        // given
        given(tokenDecoder.decode(eq(TokenType.ACCESS), anyString())).willReturn(Optional.of(사용자_ID_클레임));

        // when & then
        mockMvc.perform(post("/reports/chat-rooms")
                       .header(HttpHeaders.AUTHORIZATION, 엑세스_토큰_값)
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(채팅방_아이디가_null인_신고_request))
               )
               .andExpectAll(
                       status().isBadRequest(),
                       jsonPath("$.message").exists()
               );
    }

    @Test
    void 채팅방_아이디가_음수인_경우_신고시_400을_반환한다() throws Exception {
        // given
        given(tokenDecoder.decode(eq(TokenType.ACCESS), anyString())).willReturn(Optional.of(사용자_ID_클레임));

        // when & then
        mockMvc.perform(post("/reports/chat-rooms")
                       .header(HttpHeaders.AUTHORIZATION, 엑세스_토큰_값)
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(채팅방_아이디가_음수인_신고_request))
               )
               .andExpectAll(
                       status().isBadRequest(),
                       jsonPath("$.message").exists()
               );
    }

    @ParameterizedTest
    @MethodSource("provideChatRoomReportRequestWithEmptyDescription")
    void 신고_내용_없이_채팅방_신고시_400을_반환한다(final CreateChatRoomReportRequest 채팅방_신고_요청) throws Exception {
        // given
        given(tokenDecoder.decode(eq(TokenType.ACCESS), anyString())).willReturn(Optional.of(사용자_ID_클레임));

        // when & then
        mockMvc.perform(post("/reports/chat-rooms")
                       .header(HttpHeaders.AUTHORIZATION, 엑세스_토큰_값)
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(채팅방_신고_요청))
               )
               .andExpectAll(
                       status().isBadRequest(),
                       jsonPath("$.message").exists()
               );
    }

    private static Stream<CreateChatRoomReportRequest> provideChatRoomReportRequestWithEmptyDescription() {
        return Stream.of(신고_내용이_null인_채팅_신고_request, 신고_내용이_빈값인_채팅_신고_request);
    }

    @Test
    void 전체_채팅방_신고_목록을_조회한다() throws Exception {
        // given
        given(tokenDecoder.decode(eq(TokenType.ACCESS), anyString())).willReturn(Optional.of(사용자_ID_클레임));
        given(chatRoomReportService.readAll()).willReturn(List.of(채팅방_신고_dto1, 채팅방_신고_dto2, 채팅방_신고_dto3));

        // when & then
        final ResultActions resultActions =
                mockMvc.perform(get("/reports/chat-rooms")
                               .contentType(MediaType.APPLICATION_JSON)
                       )
                       .andExpectAll(
                               status().isOk(),
                               jsonPath("$.reports.[0].id", is(채팅방_신고_dto1.id()), Long.class),
                               jsonPath("$.reports.[0].reporter.id", is(채팅방_신고_dto1.reporterDto().id()), Long.class),
                               jsonPath("$.reports.[0].reporter.name", is(채팅방_신고_dto1.reporterDto().name())),
                               jsonPath("$.reports.[0].createdTime").exists(),
                               jsonPath("$.reports.[0].chatRoom.id", is(채팅방_신고_dto1.chatRoomDto().id()), Long.class),
                               jsonPath("$.reports.[0].description", is(채팅방_신고_dto1.description())),
                               jsonPath("$.reports.[1].id", is(채팅방_신고_dto2.id()), Long.class),
                               jsonPath("$.reports.[1].reporter.id", is(채팅방_신고_dto2.reporterDto().id()), Long.class),
                               jsonPath("$.reports.[1].reporter.name", is(채팅방_신고_dto2.reporterDto().name())),
                               jsonPath("$.reports.[1].createdTime").exists(),
                               jsonPath("$.reports.[1].chatRoom.id", is(채팅방_신고_dto2.chatRoomDto().id()), Long.class),
                               jsonPath("$.reports.[1].description", is(채팅방_신고_dto2.description())),
                               jsonPath("$.reports.[2].id", is(채팅방_신고_dto3.id()), Long.class),
                               jsonPath("$.reports.[2].reporter.id", is(채팅방_신고_dto3.reporterDto().id()), Long.class),
                               jsonPath("$.reports.[2].reporter.name", is(채팅방_신고_dto3.reporterDto().name())),
                               jsonPath("$.reports.[2].createdTime").exists(),
                               jsonPath("$.reports.[2].chatRoom.id", is(채팅방_신고_dto3.chatRoomDto().id()), Long.class),
                               jsonPath("$.reports.[2].description", is(채팅방_신고_dto3.description()))
                       );

        readAllChatRoomReport_문서화(resultActions);
    }

    @Test
    void 질문_신고를_등록한다() throws Exception {
        // given
        given(tokenDecoder.decode(eq(TokenType.ACCESS), anyString())).willReturn(Optional.of(사용자_ID_클레임));
        given(questionReportService.create(any(CreateQuestionReportDto.class))).willReturn(생성된_질문_신고_아이디);

        // when & then
        final ResultActions resultActions =
                mockMvc.perform(post("/reports/questions")
                               .header(HttpHeaders.AUTHORIZATION, 엑세스_토큰_값)
                               .contentType(MediaType.APPLICATION_JSON)
                               .content(objectMapper.writeValueAsString(질문_신고_request))
                       )
                       .andExpectAll(
                               status().isCreated(),
                               header().string(HttpHeaders.LOCATION, is("/auctions/1/questions"))
                       );

        createQuestionReport_문서화(resultActions);
    }

    @Test
    void 존재하지_않은_사용자가_질문을_신고할시_404를_반환한다() throws Exception {
        // given
        given(tokenDecoder.decode(eq(TokenType.ACCESS), anyString())).willReturn(Optional.of(존재하지_않는_사용자_ID_클레임));
        given(questionReportService.create(any(CreateQuestionReportDto.class)))
                .willThrow(new UserNotFoundException("해당 사용자를 찾을 수 없습니다."));

        // when & then
        mockMvc.perform(post("/reports/questions")
                       .header(HttpHeaders.AUTHORIZATION, 엑세스_토큰_값)
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(질문_신고_request))
               )
               .andExpectAll(
                       status().isNotFound(),
                       jsonPath("$.message").exists()
               );
    }

    @Test
    void 존재하지_않은_질문을_신고할시_404를_반환한다() throws Exception {
        // given
        given(tokenDecoder.decode(eq(TokenType.ACCESS), anyString())).willReturn(Optional.of(사용자_ID_클레임));
        given(questionReportService.create(any(CreateQuestionReportDto.class)))
                .willThrow(new QuestionNotFoundException("해당 질문을 찾을 수 없습니다."));

        // when & then
        mockMvc.perform(post("/reports/questions")
                       .header(HttpHeaders.AUTHORIZATION, 엑세스_토큰_값)
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(존재하지_않는_질문_신고_request))
               )
               .andExpectAll(
                       status().isNotFound(),
                       jsonPath("$.message").exists()
               );
    }

    @Test
    void 질문_작성자_본인이_자신의_질문을_신고할시_400을_반환한다() throws Exception {
        // given
        given(tokenDecoder.decode(eq(TokenType.ACCESS), anyString())).willReturn(Optional.of(사용자_ID_클레임));
        given(questionReportService.create(any(CreateQuestionReportDto.class)))
                .willThrow(new InvalidQuestionReportException("본인 질문입니다."));

        // when & then
        mockMvc.perform(post("/reports/questions")
                       .header(HttpHeaders.AUTHORIZATION, 엑세스_토큰_값)
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(본인의_질문_신고_request))
               )
               .andExpectAll(
                       status().isBadRequest(),
                       jsonPath("$.message").exists()
               );
    }

    @Test
    void 이미_신고한_사용자가_동일_질문을_신고할시_400을_반환한다() throws Exception {
        // given
        given(tokenDecoder.decode(eq(TokenType.ACCESS), anyString())).willReturn(Optional.of(사용자_ID_클레임));
        given(questionReportService.create(any(CreateQuestionReportDto.class)))
                .willThrow(new InvalidQuestionReportException("이미 신고한 질문입니다."));

        // when & then
        mockMvc.perform(post("/reports/questions")
                       .header(HttpHeaders.AUTHORIZATION, 엑세스_토큰_값)
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(이미_신고한_질문_신고_request))
               )
               .andExpectAll(
                       status().isBadRequest(),
                       jsonPath("$.message").exists()
               );
    }

    @Test
    void 경매_아이디가_없는_경우_질문_신고시_400을_반환한다() throws Exception {
        // given
        given(tokenDecoder.decode(eq(TokenType.ACCESS), anyString())).willReturn(Optional.of(사용자_ID_클레임));

        // when & then
        mockMvc.perform(post("/reports/questions")
                       .header(HttpHeaders.AUTHORIZATION, 엑세스_토큰_값)
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(경매_아이디가_null인_질문_신고_request))
               )
               .andExpectAll(
                       status().isBadRequest(),
                       jsonPath("$.message").exists()
               );
    }

    @Test
    void 경매_아이디가_음수인_경우_질문_신고시_400을_반환한다() throws Exception {
        // given
        given(tokenDecoder.decode(eq(TokenType.ACCESS), anyString())).willReturn(Optional.of(사용자_ID_클레임));

        // when & then
        mockMvc.perform(post("/reports/questions")
                       .header(HttpHeaders.AUTHORIZATION, 엑세스_토큰_값)
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(경매_아이디가_음수인_질문_신고_request))
               )
               .andExpectAll(
                       status().isBadRequest(),
                       jsonPath("$.message").exists()
               );
    }

    @Test
    void 질문_아이디가_없는_경우_질문_신고시_400을_반환한다() throws Exception {
        // given
        given(tokenDecoder.decode(eq(TokenType.ACCESS), anyString())).willReturn(Optional.of(사용자_ID_클레임));

        // when & then
        mockMvc.perform(post("/reports/questions")
                       .header(HttpHeaders.AUTHORIZATION, 엑세스_토큰_값)
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(질문_아이디가_null인_질문_신고_request))
               )
               .andExpectAll(
                       status().isBadRequest(),
                       jsonPath("$.message").exists()
               );
    }

    @Test
    void 질문_아이디가_음수인_경우_질문_신고시_400을_반환한다() throws Exception {
        // given
        given(tokenDecoder.decode(eq(TokenType.ACCESS), anyString())).willReturn(Optional.of(사용자_ID_클레임));

        // when & then
        mockMvc.perform(post("/reports/questions")
                       .header(HttpHeaders.AUTHORIZATION, 엑세스_토큰_값)
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(질문_아이디가_음수인_질문_신고_request))
               )
               .andExpectAll(
                       status().isBadRequest(),
                       jsonPath("$.message").exists()
               );
    }

    @ParameterizedTest
    @MethodSource("provideQuestionReportRequestWithEmptyDescription")
    void 신고_내용_없이_질문_신고시_400을_반환한다(final CreateQuestionReportRequest 질문_신고_요청) throws Exception {
        // given
        given(tokenDecoder.decode(eq(TokenType.ACCESS), anyString())).willReturn(Optional.of(사용자_ID_클레임));

        // when & then
        mockMvc.perform(post("/reports/questions")
                       .header(HttpHeaders.AUTHORIZATION, 엑세스_토큰_값)
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(질문_신고_요청))
               )
               .andExpectAll(
                       status().isBadRequest(),
                       jsonPath("$.message").exists()
               );
    }

    private static Stream<CreateQuestionReportRequest> provideQuestionReportRequestWithEmptyDescription() {
        return Stream.of(신고_내용이_null인_질문_신고_request, 신고_내용이_빈값인_질문_신고_request);
    }

    @Test
    void 전체_질문_신고_목록을_조회한다() throws Exception {
        // given
        given(tokenDecoder.decode(eq(TokenType.ACCESS), anyString())).willReturn(Optional.of(사용자_ID_클레임));
        given(questionReportService.readAll()).willReturn(List.of(질문_신고_dto1, 질문_신고_dto2, 질문_신고_dto3));

        // when & then
        final ResultActions resultActions =
                mockMvc.perform(get("/reports/questions")
                               .contentType(MediaType.APPLICATION_JSON)
                       )
                       .andExpectAll(
                               status().isOk(),
                               jsonPath("$.reports.[0].id", is(질문_신고_dto1.id()), Long.class),
                               jsonPath("$.reports.[0].reporter.id", is(질문_신고_dto1.reporterDto().id()), Long.class),
                               jsonPath("$.reports.[0].reporter.name", is(질문_신고_dto1.reporterDto().name())),
                               jsonPath("$.reports.[0].createdTime").exists(),
                               jsonPath("$.reports.[0].question.id", is(질문_신고_dto1.questionDto().id()), Long.class),
                               jsonPath("$.reports.[0].description", is(질문_신고_dto1.description())),
                               jsonPath("$.reports.[1].id", is(질문_신고_dto2.id()), Long.class),
                               jsonPath("$.reports.[1].reporter.id", is(질문_신고_dto2.reporterDto().id()), Long.class),
                               jsonPath("$.reports.[1].reporter.name", is(질문_신고_dto2.reporterDto().name())),
                               jsonPath("$.reports.[1].createdTime").exists(),
                               jsonPath("$.reports.[1].question.id", is(질문_신고_dto2.questionDto().id()), Long.class),
                               jsonPath("$.reports.[1].description", is(질문_신고_dto2.description())),
                               jsonPath("$.reports.[2].id", is(질문_신고_dto3.id()), Long.class),
                               jsonPath("$.reports.[2].reporter.id", is(질문_신고_dto3.reporterDto().id()), Long.class),
                               jsonPath("$.reports.[2].reporter.name", is(질문_신고_dto3.reporterDto().name())),
                               jsonPath("$.reports.[2].createdTime").exists(),
                               jsonPath("$.reports.[2].question.id", is(질문_신고_dto3.questionDto().id()), Long.class),
                               jsonPath("$.reports.[2].description", is(질문_신고_dto3.description()))
                       );

        readAllQuestionReport_문서화(resultActions);
    }

    @Test
    void 답변_신고를_등록한다() throws Exception {
        // given
        given(tokenDecoder.decode(eq(TokenType.ACCESS), anyString())).willReturn(Optional.of(사용자_ID_클레임));
        given(answerReportService.create(any(CreateAnswerReportDto.class))).willReturn(생성된_답변_신고_아이디);

        // when & then
        final ResultActions resultActions =
                mockMvc.perform(post("/reports/answers")
                               .header(HttpHeaders.AUTHORIZATION, 엑세스_토큰_값)
                               .contentType(MediaType.APPLICATION_JSON)
                               .content(objectMapper.writeValueAsString(답변_신고_request))
                       )
                       .andExpectAll(
                               status().isCreated(),
                               header().string(HttpHeaders.LOCATION, is("/auctions/1/questions"))
                       );

        createAnswerReport_문서화(resultActions);
    }

    @Test
    void 존재하지_않은_사용자가_답변을_신고할시_404를_반환한다() throws Exception {
        // given
        given(tokenDecoder.decode(eq(TokenType.ACCESS), anyString())).willReturn(Optional.of(존재하지_않는_사용자_ID_클레임));
        given(answerReportService.create(any(CreateAnswerReportDto.class)))
                .willThrow(new UserNotFoundException("해당 사용자를 찾을 수 없습니다."));

        // when & then
        mockMvc.perform(post("/reports/answers")
                       .header(HttpHeaders.AUTHORIZATION, 엑세스_토큰_값)
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(답변_신고_request))
               )
               .andExpectAll(
                       status().isNotFound(),
                       jsonPath("$.message").exists()
               );
    }

    @Test
    void 존재하지_않은_답변을_신고할시_404를_반환한다() throws Exception {
        // given
        given(tokenDecoder.decode(eq(TokenType.ACCESS), anyString())).willReturn(Optional.of(사용자_ID_클레임));
        given(answerReportService.create(any(CreateAnswerReportDto.class)))
                .willThrow(new AnswerNotFoundException("해당 질문을 찾을 수 없습니다."));

        // when & then
        mockMvc.perform(post("/reports/answers")
                       .header(HttpHeaders.AUTHORIZATION, 엑세스_토큰_값)
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(존재하지_않는_답변_신고_request))
               )
               .andExpectAll(
                       status().isNotFound(),
                       jsonPath("$.message").exists()
               );
    }

    @Test
    void 질문_작성자_본인이_자신의_답변을_신고할시_400을_반환한다() throws Exception {
        // given
        given(tokenDecoder.decode(eq(TokenType.ACCESS), anyString())).willReturn(Optional.of(사용자_ID_클레임));
        given(answerReportService.create(any(CreateAnswerReportDto.class)))
                .willThrow(new InvalidAnswererException("본인 질문입니다."));

        // when & then
        mockMvc.perform(post("/reports/answers")
                       .header(HttpHeaders.AUTHORIZATION, 엑세스_토큰_값)
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(본인의_답변_신고_request))
               )
               .andExpectAll(
                       status().isBadRequest(),
                       jsonPath("$.message").exists()
               );
    }

    @Test
    void 이미_신고한_사용자가_동일_답변을_신고할시_400을_반환한다() throws Exception {
        // given
        given(tokenDecoder.decode(eq(TokenType.ACCESS), anyString())).willReturn(Optional.of(사용자_ID_클레임));
        given(answerReportService.create(any(CreateAnswerReportDto.class)))
                .willThrow(new InvalidAnswererException("이미 신고한 질문입니다."));

        // when & then
        mockMvc.perform(post("/reports/answers")
                       .header(HttpHeaders.AUTHORIZATION, 엑세스_토큰_값)
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(이미_신고한_답변_신고_request))
               )
               .andExpectAll(
                       status().isBadRequest(),
                       jsonPath("$.message").exists()
               );
    }

    @Test
    void 경매_아이디가_없는_경우_답변_신고시_400을_반환한다() throws Exception {
        // given
        given(tokenDecoder.decode(eq(TokenType.ACCESS), anyString())).willReturn(Optional.of(사용자_ID_클레임));

        // when & then
        mockMvc.perform(post("/reports/answers")
                       .header(HttpHeaders.AUTHORIZATION, 엑세스_토큰_값)
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(경매_아이디가_null인_답변_신고_request))
               )
               .andExpectAll(
                       status().isBadRequest(),
                       jsonPath("$.message").exists()
               );
    }

    @Test
    void 경매_아이디가_음수인_경우_답변_신고시_400을_반환한다() throws Exception {
        // given
        given(tokenDecoder.decode(eq(TokenType.ACCESS), anyString())).willReturn(Optional.of(사용자_ID_클레임));

        // when & then
        mockMvc.perform(post("/reports/answers")
                       .header(HttpHeaders.AUTHORIZATION, 엑세스_토큰_값)
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(경매_아이디가_음수인_답변_신고_request))
               )
               .andExpectAll(
                       status().isBadRequest(),
                       jsonPath("$.message").exists()
               );
    }

    @Test
    void 답변_아이디가_없는_경우_질문_신고시_400을_반환한다() throws Exception {
        // given
        given(tokenDecoder.decode(eq(TokenType.ACCESS), anyString())).willReturn(Optional.of(사용자_ID_클레임));

        // when & then
        mockMvc.perform(post("/reports/answers")
                       .header(HttpHeaders.AUTHORIZATION, 엑세스_토큰_값)
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(질문_아이디가_null인_답변_신고_request))
               )
               .andExpectAll(
                       status().isBadRequest(),
                       jsonPath("$.message").exists()
               );
    }

    @Test
    void 질문_아이디가_음수인_경우_답변_신고시_400을_반환한다() throws Exception {
        // given
        given(tokenDecoder.decode(eq(TokenType.ACCESS), anyString())).willReturn(Optional.of(사용자_ID_클레임));

        // when & then
        mockMvc.perform(post("/reports/answers")
                       .header(HttpHeaders.AUTHORIZATION, 엑세스_토큰_값)
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(답변_아이디가_음수인_질문_신고_request))
               )
               .andExpectAll(
                       status().isBadRequest(),
                       jsonPath("$.message").exists()
               );
    }

    @ParameterizedTest
    @MethodSource("provideAnswerReportRequestWithEmptyDescription")
    void 신고_내용_없이_답변_신고시_400을_반환한다(final CreateAnswerReportRequest 답변_신고_요청) throws Exception {
        // given
        given(tokenDecoder.decode(eq(TokenType.ACCESS), anyString())).willReturn(Optional.of(사용자_ID_클레임));

        // when & then
        mockMvc.perform(post("/reports/answers")
                       .header(HttpHeaders.AUTHORIZATION, 엑세스_토큰_값)
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(답변_신고_요청))
               )
               .andExpectAll(
                       status().isBadRequest(),
                       jsonPath("$.message").exists()
               );
    }

    private static Stream<CreateAnswerReportRequest> provideAnswerReportRequestWithEmptyDescription() {
        return Stream.of(신고_내용이_null인_답변_신고_request, 신고_내용이_빈값인_답변_신고_request);
    }

    @Test
    void 전체_답변_신고_목록을_조회한다() throws Exception {
        // given
        given(tokenDecoder.decode(eq(TokenType.ACCESS), anyString())).willReturn(Optional.of(사용자_ID_클레임));
        given(answerReportService.readAll()).willReturn(List.of(답변_신고_dto1, 답변_신고_dto2, 답변_신고_dto3));

        // when & then
        final ResultActions resultActions =
                mockMvc.perform(get("/reports/answers")
                               .contentType(MediaType.APPLICATION_JSON)
                       )
                       .andExpectAll(
                               status().isOk(),
                               jsonPath("$.reports.[0].id", is(답변_신고_dto1.id()), Long.class),
                               jsonPath("$.reports.[0].reporter.id", is(답변_신고_dto1.reporterDto().id()), Long.class),
                               jsonPath("$.reports.[0].reporter.name", is(답변_신고_dto1.reporterDto().name())),
                               jsonPath("$.reports.[0].createdTime").exists(),
                               jsonPath("$.reports.[0].answer.id", is(답변_신고_dto1.answerDto().id()), Long.class),
                               jsonPath("$.reports.[0].description", is(답변_신고_dto1.description())),
                               jsonPath("$.reports.[1].id", is(답변_신고_dto2.id()), Long.class),
                               jsonPath("$.reports.[1].reporter.id", is(답변_신고_dto2.reporterDto().id()), Long.class),
                               jsonPath("$.reports.[1].reporter.name", is(답변_신고_dto2.reporterDto().name())),
                               jsonPath("$.reports.[1].createdTime").exists(),
                               jsonPath("$.reports.[1].answer.id", is(답변_신고_dto2.answerDto().id()), Long.class),
                               jsonPath("$.reports.[1].description", is(답변_신고_dto2.description())),
                               jsonPath("$.reports.[2].id", is(답변_신고_dto3.id()), Long.class),
                               jsonPath("$.reports.[2].reporter.id", is(답변_신고_dto3.reporterDto().id()), Long.class),
                               jsonPath("$.reports.[2].reporter.name", is(답변_신고_dto3.reporterDto().name())),
                               jsonPath("$.reports.[2].createdTime").exists(),
                               jsonPath("$.reports.[2].answer.id", is(답변_신고_dto3.answerDto().id()), Long.class),
                               jsonPath("$.reports.[2].description", is(답변_신고_dto3.description()))
                       );

        readAllAnswerReport_문서화(resultActions);
    }

    private void createAuctionReport_문서화(final ResultActions resultActions) throws Exception {
        resultActions.andDo(
                restDocs.document(
                        requestHeaders(
                                headerWithName("Authorization").description("회원 Bearer 인증 정보")
                        ),
                        requestFields(
                                fieldWithPath("auctionId").description("신고할 경매 ID"),
                                fieldWithPath("description").description("신고 내용")
                        )
                )
        );
    }

    private void readAllAuctionReport_문서화(final ResultActions resultActions) throws Exception {
        resultActions.andDo(
                restDocs.document(
                        responseFields(
                                fieldWithPath("reports.[]").type(JsonFieldType.ARRAY)
                                                           .description("모든 경매 신고 목록"),
                                fieldWithPath("reports.[].id").type(JsonFieldType.NUMBER)
                                                              .description("경매 신고 글 ID"),
                                fieldWithPath("reports.[].reporter.id").type(JsonFieldType.NUMBER)
                                                                       .description("경매 신고한 사용자의 ID"),
                                fieldWithPath("reports.[].reporter.name").type(JsonFieldType.STRING)
                                                                         .description("경매 신고한 사용자의 이름"),
                                fieldWithPath("reports.[].createdTime").type(JsonFieldType.STRING)
                                                                       .description("경매 신고 시간"),
                                fieldWithPath("reports.[].auction.id").type(JsonFieldType.NUMBER)
                                                                      .description("신고한 경매 ID"),
                                fieldWithPath("reports.[].auction.title").type(JsonFieldType.STRING)
                                                                         .description("신고한 경매 제목"),
                                fieldWithPath("reports.[].description").type(JsonFieldType.STRING)
                                                                       .description("신고 내용")
                        )
                )
        );
    }

    private void createChatRoomReport_문서화(final ResultActions resultActions) throws Exception {
        resultActions.andDo(
                restDocs.document(
                        requestHeaders(
                                headerWithName("Authorization").description("회원 Bearer 인증 정보")
                        ),
                        requestFields(
                                fieldWithPath("chatRoomId").description("신고할 채팅방 ID"),
                                fieldWithPath("description").description("신고 내용")
                        )
                )
        );
    }

    private void readAllChatRoomReport_문서화(final ResultActions resultActions) throws Exception {
        resultActions.andDo(
                restDocs.document(
                        responseFields(
                                fieldWithPath("reports.[]").type(JsonFieldType.ARRAY)
                                                           .description("모든 채팅방 신고 목록"),
                                fieldWithPath("reports.[].id").type(JsonFieldType.NUMBER)
                                                              .description("채팅방 신고 글 ID"),
                                fieldWithPath("reports.[].reporter.id").type(JsonFieldType.NUMBER)
                                                                       .description("채팅방을 신고한 사용자의 ID"),
                                fieldWithPath("reports.[].reporter.name").type(JsonFieldType.STRING)
                                                                         .description("채팅방을 신고한 사용자의 이름"),
                                fieldWithPath("reports.[].createdTime").type(JsonFieldType.STRING)
                                                                       .description("채팅방 신고 시간"),
                                fieldWithPath("reports.[].chatRoom.id").type(JsonFieldType.NUMBER)
                                                                       .description("신고한 채팅방 ID"),
                                fieldWithPath("reports.[].description").type(JsonFieldType.STRING)
                                                                       .description("신고 내용")
                        )
                )
        );
    }

    private void createQuestionReport_문서화(final ResultActions resultActions) throws Exception {
        resultActions.andDo(
                restDocs.document(
                        requestHeaders(
                                headerWithName("Authorization").description("회원 Bearer 인증 정보")
                        ),
                        requestFields(
                                fieldWithPath("auctionId").description("질문의 경매 ID"),
                                fieldWithPath("questionId").description("신고할 질문 ID"),
                                fieldWithPath("description").description("신고 내용")
                        )
                )
        );
    }

    private void readAllQuestionReport_문서화(final ResultActions resultActions) throws Exception {
        resultActions.andDo(
                restDocs.document(
                        responseFields(
                                fieldWithPath("reports.[]").type(JsonFieldType.ARRAY)
                                                           .description("모든 질문 신고 목록"),
                                fieldWithPath("reports.[].id").type(JsonFieldType.NUMBER)
                                                              .description("질문 신고 글 ID"),
                                fieldWithPath("reports.[].reporter.id").type(JsonFieldType.NUMBER)
                                                                       .description("질문을 신고한 사용자의 ID"),
                                fieldWithPath("reports.[].reporter.name").type(JsonFieldType.STRING)
                                                                         .description("질문을 신고한 사용자의 이름"),
                                fieldWithPath("reports.[].createdTime").type(JsonFieldType.STRING)
                                                                       .description("질문 신고 시간"),
                                fieldWithPath("reports.[].question.id").type(JsonFieldType.NUMBER)
                                                                       .description("신고한 질문 ID"),
                                fieldWithPath("reports.[].description").type(JsonFieldType.STRING)
                                                                       .description("신고 내용")
                        )
                )
        );
    }

    private void createAnswerReport_문서화(final ResultActions resultActions) throws Exception {
        resultActions.andDo(
                restDocs.document(
                        requestHeaders(
                                headerWithName("Authorization").description("회원 Bearer 인증 정보")
                        ),
                        requestFields(
                                fieldWithPath("auctionId").description("질문의 경매 ID"),
                                fieldWithPath("answerId").description("신고할 답변 ID"),
                                fieldWithPath("description").description("신고 내용")
                        )
                )
        );
    }

    private void readAllAnswerReport_문서화(final ResultActions resultActions) throws Exception {
        resultActions.andDo(
                restDocs.document(
                        responseFields(
                                fieldWithPath("reports.[]").type(JsonFieldType.ARRAY)
                                                           .description("모든 답변 신고 목록"),
                                fieldWithPath("reports.[].id").type(JsonFieldType.NUMBER)
                                                              .description("답변 신고 글 ID"),
                                fieldWithPath("reports.[].reporter.id").type(JsonFieldType.NUMBER)
                                                                       .description("답변을 신고한 사용자의 ID"),
                                fieldWithPath("reports.[].reporter.name").type(JsonFieldType.STRING)
                                                                         .description("답변을 신고한 사용자의 이름"),
                                fieldWithPath("reports.[].createdTime").type(JsonFieldType.STRING)
                                                                       .description("답변 신고 시간"),
                                fieldWithPath("reports.[].answer.id").type(JsonFieldType.NUMBER)
                                                                     .description("신고한 답변 ID"),
                                fieldWithPath("reports.[].description").type(JsonFieldType.STRING)
                                                                       .description("신고 내용")
                        )
                )
        );
    }
}
