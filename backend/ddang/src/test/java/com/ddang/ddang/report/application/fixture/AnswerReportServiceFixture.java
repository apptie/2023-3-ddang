package com.ddang.ddang.report.application.fixture;

import com.ddang.ddang.auction.domain.Auction;
import com.ddang.ddang.auction.domain.BidUnit;
import com.ddang.ddang.auction.domain.Price;
import com.ddang.ddang.auction.domain.repository.AuctionRepository;
import com.ddang.ddang.category.domain.Category;
import com.ddang.ddang.category.infrastructure.persistence.JpaCategoryRepository;
import com.ddang.ddang.image.domain.AuctionImage;
import com.ddang.ddang.image.domain.ProfileImage;
import com.ddang.ddang.qna.domain.Answer;
import com.ddang.ddang.qna.domain.Question;
import com.ddang.ddang.qna.domain.repository.AnswerRepository;
import com.ddang.ddang.qna.domain.repository.QuestionRepository;
import com.ddang.ddang.report.application.dto.request.CreateAnswerReportDto;
import com.ddang.ddang.report.domain.AnswerReport;
import com.ddang.ddang.report.domain.repository.AnswerReportRepository;
import com.ddang.ddang.user.domain.Reliability;
import com.ddang.ddang.user.domain.User;
import com.ddang.ddang.user.domain.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;

@SuppressWarnings("NonAsciiCharacters")
public class AnswerReportServiceFixture {

    @Autowired
    private JpaCategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuctionRepository auctionRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private AnswerRepository answerRepository;

    @Autowired
    private AnswerReportRepository answerReportRepository;

    protected User 신고자;
    protected User 이미_신고한_신고자1;
    protected User 이미_신고한_신고자2;
    protected User 이미_신고한_신고자3;
    protected Answer 답변;
    protected AnswerReport 답변_신고1;
    protected AnswerReport 답변_신고2;
    protected AnswerReport 답변_신고3;
    protected CreateAnswerReportDto 답변_신고_요청_dto;
    protected CreateAnswerReportDto 존재하지_않는_답변_신고_요청_dto;
    protected CreateAnswerReportDto 존재하지_않는_사용자가_답변_신고_요청_dto;
    protected CreateAnswerReportDto 답변자가_본인_답변_신고_요청_dto;
    protected CreateAnswerReportDto 이미_신고한_답변_신고_요청_dto;

    @BeforeEach
    void setUp() {
        final Long 존재하지_않는_답변_아이디 = -999L;
        final Long 존재하지_않는_사용자_아이디 = -999L;

        final User 판매자 = User.builder()
                             .name("판매자")
                             .profileImage(new ProfileImage("upload.png", "store.png"))
                             .reliability(new Reliability(4.7d))
                             .oauthId("12345")
                             .build();
        final User 질문자 = User.builder()
                             .name("질문자")
                             .profileImage(new ProfileImage("upload.png", "store.png"))
                             .reliability(new Reliability(4.7d))
                             .oauthId("12346")
                             .build();
        final User 답변자 = 판매자;
        신고자 = User.builder()
                  .name("신고자")
                  .profileImage(new ProfileImage("upload.png", "store.png"))
                  .reliability(new Reliability(4.7d))
                  .oauthId("12347")
                  .build();
        이미_신고한_신고자1 = User.builder()
                          .name("이미 신고한 신고자1")
                          .profileImage(new ProfileImage("upload.png", "store.png"))
                          .reliability(new Reliability(4.7d))
                          .oauthId("12348")
                          .build();
        이미_신고한_신고자2 = User.builder()
                          .name("이미 신고한 신고자2")
                          .profileImage(new ProfileImage("upload.png", "store.png"))
                          .reliability(new Reliability(4.7d))
                          .oauthId("12349")
                          .build();
        이미_신고한_신고자3 = User.builder()
                          .name("이미 신고한 신고자3")
                          .profileImage(new ProfileImage("upload.png", "store.png"))
                          .reliability(new Reliability(4.7d))
                          .oauthId("12350")
                          .build();

        final Category 전자기기_카테고리 = new Category("전자기기");
        final Category 전자기기_서브_노트북_카테고리 = new Category("노트북 카테고리");
        전자기기_카테고리.addSubCategory(전자기기_서브_노트북_카테고리);
        final AuctionImage 경매_이미지 = new AuctionImage("경매이미지.jpg", "경매이미지.jpg");
        final Auction 경매 = Auction.builder()
                                  .seller(판매자)
                                  .title("경매 상품")
                                  .description("이것은 경매 상품입니다.")
                                  .subCategory(전자기기_서브_노트북_카테고리)
                                  .bidUnit(new BidUnit(1_000))
                                  .startPrice(new Price(1_000))
                                  .closingTime(LocalDateTime.now())
                                  .build();
        경매.addAuctionImages(List.of(경매_이미지));

        final Question 질문 = new Question(경매, 질문자, "질문드립니다.");
        답변 = new Answer(판매자, "답변드립니다.");
        질문.addAnswer(답변);
        답변_신고1 = new AnswerReport(이미_신고한_신고자1, 답변, "신고합니다.");
        답변_신고2 = new AnswerReport(이미_신고한_신고자2, 답변, "신고합니다.");
        답변_신고3 = new AnswerReport(이미_신고한_신고자3, 답변, "신고합니다.");

        userRepository.save(판매자);
        userRepository.save(질문자);
        userRepository.save(답변자);
        userRepository.save(신고자);
        userRepository.save(이미_신고한_신고자1);
        userRepository.save(이미_신고한_신고자2);
        userRepository.save(이미_신고한_신고자3);

        categoryRepository.saveAll(List.of(전자기기_카테고리, 전자기기_서브_노트북_카테고리));
        auctionRepository.save(경매);
        questionRepository.save(질문);
        answerRepository.save(답변);
        answerReportRepository.save(답변_신고1);
        answerReportRepository.save(답변_신고2);
        answerReportRepository.save(답변_신고3);

        답변_신고_요청_dto = new CreateAnswerReportDto(답변.getId(), "신고합니다.", 신고자.getId());
        존재하지_않는_답변_신고_요청_dto = new CreateAnswerReportDto(존재하지_않는_답변_아이디, "신고합니다.", 신고자.getId());
        존재하지_않는_사용자가_답변_신고_요청_dto = new CreateAnswerReportDto(답변.getId(), "신고합니다.", 존재하지_않는_사용자_아이디);
        답변자가_본인_답변_신고_요청_dto = new CreateAnswerReportDto(답변.getId(), "신고합니다.", 답변자.getId());
        이미_신고한_답변_신고_요청_dto = new CreateAnswerReportDto(답변.getId(), "신고합니다.", 이미_신고한_신고자1.getId());
    }
}
