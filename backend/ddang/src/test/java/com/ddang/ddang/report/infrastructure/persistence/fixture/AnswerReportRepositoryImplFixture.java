package com.ddang.ddang.report.infrastructure.persistence.fixture;

import com.ddang.ddang.auction.domain.Auction;
import com.ddang.ddang.auction.domain.BidUnit;
import com.ddang.ddang.auction.domain.Price;
import com.ddang.ddang.auction.domain.repository.AuctionRepository;
import com.ddang.ddang.auction.infrastructure.persistence.AuctionRepositoryImpl;
import com.ddang.ddang.auction.infrastructure.persistence.JpaAuctionRepository;
import com.ddang.ddang.auction.infrastructure.persistence.QuerydslAuctionRepository;
import com.ddang.ddang.category.domain.Category;
import com.ddang.ddang.category.infrastructure.persistence.JpaCategoryRepository;
import com.ddang.ddang.image.domain.AuctionImage;
import com.ddang.ddang.image.domain.ProfileImage;
import com.ddang.ddang.qna.domain.Answer;
import com.ddang.ddang.qna.domain.Question;
import com.ddang.ddang.qna.infrastructure.persistence.JpaAnswerRepository;
import com.ddang.ddang.qna.infrastructure.persistence.JpaQuestionRepository;
import com.ddang.ddang.report.domain.AnswerReport;
import com.ddang.ddang.report.domain.repository.AnswerReportRepository;
import com.ddang.ddang.report.infrastructure.persistence.AnswerReportRepositoryImpl;
import com.ddang.ddang.report.infrastructure.persistence.JpaAnswerReportRepository;
import com.ddang.ddang.user.domain.Reliability;
import com.ddang.ddang.user.domain.User;
import com.ddang.ddang.user.domain.repository.UserRepository;
import com.ddang.ddang.user.infrastructure.persistence.JpaUserRepository;
import com.ddang.ddang.user.infrastructure.persistence.UserRepositoryImpl;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;

@SuppressWarnings("NonAsciiCharacters")
public class AnswerReportRepositoryImplFixture {

    @Autowired
    private JpaCategoryRepository categoryRepository;

    @Autowired
    private JpaQuestionRepository questionRepository;

    @Autowired
    private JpaAnswerRepository answerRepository;

    private UserRepository userRepository;

    private AuctionRepository auctionRepository;

    private AnswerReportRepository answerReportRepository;

    protected User 신고자;
    protected Answer 답변;
    protected String 신고_내용 = "신고합니다.";
    protected Answer 이미_신고된_답변;
    protected AnswerReport 답변_신고1;
    protected AnswerReport 답변_신고2;
    protected AnswerReport 답변_신고3;
    protected AnswerReport 답변_신고4;

    @BeforeEach
    void setUpFixture(
            @Autowired final JpaUserRepository jpaUserRepository,
            @Autowired final JpaAuctionRepository jpaAuctionRepository,
            @Autowired final JPAQueryFactory queryFactory,
            @Autowired final JpaAnswerReportRepository jpaAnswerReportRepository
    ) {
        userRepository = new UserRepositoryImpl(jpaUserRepository);
        auctionRepository = new AuctionRepositoryImpl(jpaAuctionRepository, new QuerydslAuctionRepository(queryFactory));
        answerReportRepository = new AnswerReportRepositoryImpl(jpaAnswerReportRepository);

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
        신고자 = User.builder()
                  .name("신고자")
                  .profileImage(new ProfileImage("upload.png", "store.png"))
                  .reliability(new Reliability(4.7d))
                  .oauthId("12347")
                  .build();
        final User 신고자2 = User.builder()
                              .name("신고자2")
                              .profileImage(new ProfileImage("upload.png", "store.png"))
                              .reliability(new Reliability(4.7d))
                              .oauthId("12348")
                              .build();
        final User 신고자3 = User.builder()
                              .name("신고자3")
                              .profileImage(new ProfileImage("upload.png", "store.png"))
                              .reliability(new Reliability(4.7d))
                              .oauthId("12349")
                              .build();
        final User 신고자4 = User.builder()
                              .name("신고자4")
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

        final Question 질문1 = new Question(경매, 질문자, "질문드립니다.");
        final Question 질문2 = new Question(경매, 질문자, "질문드립니다.");
        답변 = new Answer(판매자, "답변드립니다.");
        이미_신고된_답변 = new Answer(판매자, "답변드립니다.");
        질문1.addAnswer(답변);
        질문2.addAnswer(이미_신고된_답변);

        답변_신고1 = new AnswerReport(신고자, 이미_신고된_답변, "신고합니다.");
        답변_신고2 = new AnswerReport(신고자2, 이미_신고된_답변, "신고합니다.");
        답변_신고3 = new AnswerReport(신고자3, 이미_신고된_답변, "신고합니다.");
        답변_신고4 = new AnswerReport(신고자4, 이미_신고된_답변, "신고합니다.");

        userRepository.save(판매자);
        userRepository.save(질문자);
        userRepository.save(신고자);
        userRepository.save(신고자2);
        userRepository.save(신고자3);
        userRepository.save(신고자4);


        categoryRepository.saveAll(List.of(전자기기_카테고리, 전자기기_서브_노트북_카테고리));
        auctionRepository.save(경매);

        questionRepository.saveAll(List.of(질문1, 질문2));
        answerRepository.saveAll(List.of(답변, 이미_신고된_답변));
        answerReportRepository.save(답변_신고1);
        answerReportRepository.save(답변_신고2);
        answerReportRepository.save(답변_신고3);
        answerReportRepository.save(답변_신고4);
    }
}
