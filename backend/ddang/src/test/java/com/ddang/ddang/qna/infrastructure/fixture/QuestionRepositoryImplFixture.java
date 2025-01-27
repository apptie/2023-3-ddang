package com.ddang.ddang.qna.infrastructure.fixture;

import com.ddang.ddang.auction.domain.Auction;
import com.ddang.ddang.auction.domain.BidUnit;
import com.ddang.ddang.auction.domain.Price;
import com.ddang.ddang.auction.domain.repository.AuctionRepository;
import com.ddang.ddang.auction.infrastructure.persistence.AuctionRepositoryImpl;
import com.ddang.ddang.auction.infrastructure.persistence.JpaAuctionRepository;
import com.ddang.ddang.auction.infrastructure.persistence.QuerydslAuctionRepository;
import com.ddang.ddang.image.domain.ProfileImage;
import com.ddang.ddang.qna.domain.Answer;
import com.ddang.ddang.qna.domain.Question;
import com.ddang.ddang.qna.domain.repository.AnswerRepository;
import com.ddang.ddang.qna.domain.repository.QuestionRepository;
import com.ddang.ddang.qna.infrastructure.persistence.AnswerRepositoryImpl;
import com.ddang.ddang.qna.infrastructure.persistence.JpaAnswerRepository;
import com.ddang.ddang.qna.infrastructure.persistence.JpaQuestionRepository;
import com.ddang.ddang.qna.infrastructure.persistence.QuestionRepositoryImpl;
import com.ddang.ddang.user.domain.Reliability;
import com.ddang.ddang.user.domain.User;
import com.ddang.ddang.user.domain.repository.UserRepository;
import com.ddang.ddang.user.infrastructure.persistence.JpaUserRepository;
import com.ddang.ddang.user.infrastructure.persistence.UserRepositoryImpl;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

@SuppressWarnings("NonAsciiCharacters")
public class QuestionRepositoryImplFixture {

    private AuctionRepository auctionRepository;

    private UserRepository userRepository;

    private QuestionRepository questionRepository;

    private AnswerRepository answerRepository;

    protected Auction 경매;
    protected Auction 질문이_3개_답변이_2개인_경매;
    protected User 질문자;
    protected String 질문_내용 = "궁금한 점이 있어요.";
    protected Question 질문1;
    protected Question 질문2;
    protected Question 질문3;
    protected Question 삭제된_질문;
    protected Answer 답변1;
    protected Answer 답변2;

    @BeforeEach
    void setUpFixture(
            @Autowired final JpaAuctionRepository jpaAuctionRepository,
            @Autowired final JPAQueryFactory queryFactory,
            @Autowired final JpaUserRepository jpaUserRepository,
            @Autowired final JpaQuestionRepository jpaQuestionRepository,
            @Autowired final JpaAnswerRepository jpaAnswerRepository
            ) {
        auctionRepository = new AuctionRepositoryImpl(jpaAuctionRepository, new QuerydslAuctionRepository(queryFactory));
        userRepository = new UserRepositoryImpl(jpaUserRepository);
        questionRepository = new QuestionRepositoryImpl(jpaQuestionRepository);
        answerRepository = new AnswerRepositoryImpl(jpaAnswerRepository);

        final User 판매자 = User.builder()
                             .name("판매자")
                             .profileImage(new ProfileImage("upload.png", "store.png"))
                             .reliability(new Reliability(4.7d))
                             .oauthId("12345")
                             .build();
        경매 = Auction.builder()
                    .seller(판매자)
                    .title("경매 상품")
                    .description("이것은 경매 상품입니다.")
                    .bidUnit(new BidUnit(1_000))
                    .startPrice(new Price(1_000))
                    .closingTime(LocalDateTime.now())
                    .build();
        질문이_3개_답변이_2개인_경매 = Auction.builder()
                                   .seller(판매자)
                                   .title("경매 상품")
                                   .description("이것은 경매 상품입니다.")
                                   .bidUnit(new BidUnit(1_000))
                                   .startPrice(new Price(1_000))
                                   .closingTime(LocalDateTime.now())
                                   .build();
        질문자 = User.builder()
                  .name("질문자")
                  .profileImage(new ProfileImage("upload.png", "store.png"))
                  .reliability(new Reliability(4.7d))
                  .oauthId("12346")
                  .build();

        질문1 = new Question(질문이_3개_답변이_2개인_경매, 질문자, "질문1");
        질문2 = new Question(질문이_3개_답변이_2개인_경매, 질문자, "질문2");
        질문3 = new Question(질문이_3개_답변이_2개인_경매, 질문자, "질문3");
        답변1 = new Answer(판매자, "답변1");
        답변2 = new Answer(판매자, "답변2");
        질문1.addAnswer(답변1);
        질문2.addAnswer(답변2);

        삭제된_질문 = new Question(경매, 질문자, "질문3");
        삭제된_질문.delete();

        userRepository.save(판매자);
        userRepository.save(질문자);
        auctionRepository.save(경매);
        auctionRepository.save(질문이_3개_답변이_2개인_경매);
        questionRepository.save(질문1);
        questionRepository.save(질문2);
        questionRepository.save(질문3);
        questionRepository.save(삭제된_질문);
        answerRepository.save(답변1);
        answerRepository.save(답변2);
    }
}
