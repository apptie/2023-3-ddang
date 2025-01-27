package com.ddang.ddang.review.infrastructure.persistence.fixture;

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
import com.ddang.ddang.review.domain.Review;
import com.ddang.ddang.review.domain.Score;
import com.ddang.ddang.review.domain.repository.ReviewRepository;
import com.ddang.ddang.review.infrastructure.persistence.JpaReviewRepository;
import com.ddang.ddang.review.infrastructure.persistence.ReviewRepositoryImpl;
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
public class ReviewRepositoryImplFixture {

    @Autowired
    private JpaCategoryRepository categoryRepository;

    private UserRepository userRepository;

    private AuctionRepository auctionRepository;

    private ReviewRepository reviewRepository;

    protected User 판매자1;
    protected User 판매자2;
    protected User 리뷰_안한_경매_판매자;
    protected User 구매자;
    protected Auction 판매자1이_리뷰한_경매;
    protected Auction 판매자2가_리뷰한_경매;
    protected Auction 리뷰_안한_경매;
    protected Review 저장하려는_리뷰;
    protected Review 구매자가_판매자1에게_받은_리뷰;
    protected Review 구매자가_판매자2에게_받은_리뷰;
    protected Long 존재하지_않는_리뷰_id = -999L;

    @BeforeEach
    void fixtureSetUp(
            @Autowired final JPAQueryFactory jpaQueryFactory,
            @Autowired final JpaAuctionRepository jpaAuctionRepository,
            @Autowired final JpaUserRepository jpaUserRepository,
            @Autowired final JpaReviewRepository jpaReviewRepository
    ) {
        auctionRepository = new AuctionRepositoryImpl(jpaAuctionRepository, new QuerydslAuctionRepository(jpaQueryFactory));
        userRepository = new UserRepositoryImpl(jpaUserRepository);
        reviewRepository = new ReviewRepositoryImpl(jpaReviewRepository);

        final Category 전자기기_카테고리 = new Category("전자기기");
        final Category 전자기기_서브_노트북_카테고리 = new Category("노트북 카테고리");
        전자기기_카테고리.addSubCategory(전자기기_서브_노트북_카테고리);
        categoryRepository.save(전자기기_카테고리);

        final ProfileImage 리뷰_안한_판매자_프로필_이미지 = new ProfileImage("no_review_seller_profile.png", "no_review_seller_profile.png");
        final ProfileImage 판매자1_프로필_이미지 = new ProfileImage("seller1_profile.png", "seller1_profile.png");
        final ProfileImage 판매자2_프로필_이미지 = new ProfileImage("seller2_profile.png", "seller2_profile.png");
        final ProfileImage 구매자_프로필_이미지 = new ProfileImage("buyer_profile.png", "buyer_profile.png");

        판매자1 = User.builder()
                   .name("판매자1")
                   .profileImage(판매자1_프로필_이미지)
                   .reliability(new Reliability(4.7d))
                   .oauthId("12345")
                   .build();
        판매자2 = User.builder()
                   .name("판매자2")
                   .profileImage(판매자2_프로필_이미지)
                   .reliability(new Reliability(4.7d))
                   .oauthId("12346")
                   .build();
        리뷰_안한_경매_판매자 = User.builder()
                           .name("리뷰 안한 판매자")
                           .profileImage(리뷰_안한_판매자_프로필_이미지)
                           .reliability(new Reliability(4.7d))
                           .oauthId("12346")
                           .build();
        구매자 = User.builder()
                  .name("구매자")
                  .profileImage(구매자_프로필_이미지)
                  .reliability(new Reliability(4.7d))
                  .oauthId("12347")
                  .build();

        userRepository.save(판매자1);
        userRepository.save(판매자2);
        userRepository.save(리뷰_안한_경매_판매자);
        userRepository.save(구매자);

        final AuctionImage 경매1_대표_이미지 = new AuctionImage("경매1_대표_이미지.png", "경매1_대표_이미지.png");
        final AuctionImage 경매2_대표_이미지 = new AuctionImage("경매2_대표_이미지.png", "경매2_대표_이미지.png");
        final AuctionImage 리뷰_안한_경매_대표_이미지 = new AuctionImage("리뷰_안한_경매_대표_이미지.png", "리뷰_안한_경매_대표_이미지.png");

        판매자1이_리뷰한_경매 = Auction.builder()
                              .seller(판매자1)
                              .title("맥북")
                              .description("맥북 팔아요")
                              .subCategory(전자기기_서브_노트북_카테고리)
                              .startPrice(new Price(10_000))
                              .bidUnit(new BidUnit(1_000))
                              .closingTime(LocalDateTime.now())
                              .build();
        판매자2가_리뷰한_경매 = Auction.builder()
                              .seller(판매자2)
                              .title("맥북")
                              .description("맥북 팔아요")
                              .subCategory(전자기기_서브_노트북_카테고리)
                              .startPrice(new Price(10_000))
                              .bidUnit(new BidUnit(1_000))
                              .closingTime(LocalDateTime.now())
                              .build();
        리뷰_안한_경매 = Auction.builder()
                          .seller(판매자2)
                          .title("맥북")
                          .description("맥북 팔아요")
                          .subCategory(전자기기_서브_노트북_카테고리)
                          .startPrice(new Price(10_000))
                          .bidUnit(new BidUnit(1_000))
                          .closingTime(LocalDateTime.now())
                          .build();

        판매자1이_리뷰한_경매.addAuctionImages(List.of(경매1_대표_이미지));
        판매자2가_리뷰한_경매.addAuctionImages(List.of(경매2_대표_이미지));
        리뷰_안한_경매.addAuctionImages(List.of(리뷰_안한_경매_대표_이미지));
        auctionRepository.save(판매자1이_리뷰한_경매);
        auctionRepository.save(판매자2가_리뷰한_경매);
        auctionRepository.save(리뷰_안한_경매);

        저장하려는_리뷰 = Review.builder()
                         .auction(리뷰_안한_경매)
                         .writer(판매자1)
                         .target(구매자)
                         .content("친절하다.")
                         .score(new Score(5.0d))
                         .build();
        구매자가_판매자1에게_받은_리뷰 = Review.builder()
                                  .auction(판매자1이_리뷰한_경매)
                                  .writer(판매자1)
                                  .target(구매자)
                                  .content("친절하다.")
                                  .score(new Score(5.0d))
                                  .build();
        구매자가_판매자2에게_받은_리뷰 = Review.builder()
                                  .auction(판매자2가_리뷰한_경매)
                                  .writer(판매자2)
                                  .target(구매자)
                                  .content("별로다.")
                                  .score(new Score(1.0d))
                                  .build();

        reviewRepository.save(구매자가_판매자1에게_받은_리뷰);
        reviewRepository.save(구매자가_판매자2에게_받은_리뷰);
    }
}
