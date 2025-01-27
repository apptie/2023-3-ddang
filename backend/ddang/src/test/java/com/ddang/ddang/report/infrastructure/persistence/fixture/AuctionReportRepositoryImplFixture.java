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
import com.ddang.ddang.image.infrastructure.persistence.JpaAuctionImageRepository;
import com.ddang.ddang.image.infrastructure.persistence.JpaProfileImageRepository;
import com.ddang.ddang.report.domain.AuctionReport;
import com.ddang.ddang.report.domain.repository.AuctionReportRepository;
import com.ddang.ddang.report.infrastructure.persistence.AuctionReportRepositoryImpl;
import com.ddang.ddang.report.infrastructure.persistence.JpaAuctionReportRepository;
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
public class AuctionReportRepositoryImplFixture {

    @Autowired
    private JpaCategoryRepository categoryRepository;

    @Autowired
    private JpaProfileImageRepository profileImageRepository;

    @Autowired
    private JpaAuctionImageRepository auctionImageRepository;

    private UserRepository userRepository;

    private AuctionRepository auctionRepository;

    private AuctionReportRepository auctionReportRepository;

    protected Long 존재하지_않는_경매_아이디 = -9999L;
    protected Long 존재하지_않는_사용자_아이디 = -9999L;
    protected User 판매자;
    protected User 신고자1;
    protected Auction 경매;
    protected AuctionReport 경매_신고1;
    protected AuctionReport 경매_신고2;
    protected AuctionReport 경매_신고3;

    @BeforeEach
    void setUpFixture(
            @Autowired final JPAQueryFactory jpaQueryFactory,
            @Autowired final JpaUserRepository jpaUserRepository,
            @Autowired final JpaAuctionRepository jpaAuctionRepository,
            @Autowired final JpaAuctionReportRepository jpaAuctionReportRepository
    ) {
        userRepository = new UserRepositoryImpl(jpaUserRepository);
        auctionRepository = new AuctionRepositoryImpl(jpaAuctionRepository, new QuerydslAuctionRepository(jpaQueryFactory));
        auctionReportRepository = new AuctionReportRepositoryImpl(jpaAuctionReportRepository);

        판매자 = User.builder()
                  .name("판매자")
                  .profileImage(new ProfileImage("upload.png", "store.png"))
                  .reliability(new Reliability(4.7d))
                  .oauthId("12345")
                  .build();
        신고자1 = User.builder()
                   .name("신고자1")
                   .profileImage(new ProfileImage("upload.png", "store.png"))
                   .reliability(new Reliability(4.7d))
                   .oauthId("12346")
                   .build();
        final User 신고자2 = User.builder()
                              .name("신고자2")
                              .profileImage(new ProfileImage("upload.png", "store.png"))
                              .reliability(new Reliability(4.7d))
                              .oauthId("12347")
                              .build();
        final User 신고자3 = User.builder()
                              .name("신고자3")
                              .profileImage(new ProfileImage("upload.png", "store.png"))
                              .reliability(new Reliability(4.7d))
                              .oauthId("12348")
                              .build();

        final Category 전자기기_카테고리 = new Category("전자기기");
        final Category 전자기기_서브_노트북_카테고리 = new Category("노트북 카테고리");
        전자기기_카테고리.addSubCategory(전자기기_서브_노트북_카테고리);
        final AuctionImage 경매_이미지 = new AuctionImage("경매이미지.jpg", "경매이미지.jpg");
        경매 = Auction.builder()
                    .seller(판매자)
                    .title("경매 상품")
                    .description("이것은 경매 상품입니다.")
                    .subCategory(전자기기_서브_노트북_카테고리)
                    .bidUnit(new BidUnit(1_000))
                    .startPrice(new Price(1_000))
                    .closingTime(LocalDateTime.now())
                    .build();
        경매.addAuctionImages(List.of(경매_이미지));

        경매_신고1 = new AuctionReport(신고자1, 경매, "신고합니다");
        경매_신고2 = new AuctionReport(신고자2, 경매, "신고합니다");
        경매_신고3 = new AuctionReport(신고자3, 경매, "신고합니다");


        profileImageRepository.save(new ProfileImage("upload.png", "store.png"));
        userRepository.save(판매자);
        userRepository.save(신고자1);
        userRepository.save(신고자2);
        userRepository.save(신고자3);

        categoryRepository.saveAll(List.of(전자기기_카테고리, 전자기기_서브_노트북_카테고리));
        auctionImageRepository.save(경매_이미지);
        auctionRepository.save(경매);

        auctionReportRepository.save(경매_신고1);
        auctionReportRepository.save(경매_신고2);
        auctionReportRepository.save(경매_신고3);
    }
}
