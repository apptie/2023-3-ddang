package com.ddang.ddang.bid.application.fixture;

import com.ddang.ddang.auction.domain.Auction;
import com.ddang.ddang.auction.domain.BidUnit;
import com.ddang.ddang.auction.domain.Price;
import com.ddang.ddang.auction.domain.repository.AuctionRepository;
import com.ddang.ddang.bid.application.dto.request.CreateBidDto;
import com.ddang.ddang.bid.domain.Bid;
import com.ddang.ddang.bid.domain.BidPrice;
import com.ddang.ddang.bid.domain.repository.BidRepository;
import com.ddang.ddang.category.domain.Category;
import com.ddang.ddang.category.infrastructure.persistence.JpaCategoryRepository;
import com.ddang.ddang.image.domain.AuctionImage;
import com.ddang.ddang.image.domain.ProfileImage;
import com.ddang.ddang.notification.domain.NotificationStatus;
import com.ddang.ddang.region.domain.AuctionRegion;
import com.ddang.ddang.region.domain.Region;
import com.ddang.ddang.region.domain.repository.RegionRepository;
import com.ddang.ddang.user.domain.Reliability;
import com.ddang.ddang.user.domain.User;
import com.ddang.ddang.user.domain.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;

@SuppressWarnings("NonAsciiCharacters")
public class BidServiceFixture {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuctionRepository auctionRepository;

    @Autowired
    private BidRepository bidRepository;

    @Autowired
    private RegionRepository regionRepository;

    @Autowired
    private JpaCategoryRepository categoryRepository;

    protected NotificationStatus 알림_성공 = NotificationStatus.SUCCESS;
    protected String 이미지_절대_url = "https://3-ddang.store/auctions/images";
    protected Long 존재하지_않는_경매_아이디 = -999L;
    protected Long 존재하지_않는_사용자_아이디 = -9999L;

    protected User 입찰자1;
    protected User 입찰자2;
    protected Auction 경매1;
    protected Auction 입찰_내역이_없는_경매;
    protected Auction 입찰_내역이_하나_있던_경매;

    protected CreateBidDto 입찰_요청_dto;
    protected CreateBidDto 입찰_내역이_하나_존재하는_경매에_대한_입찰_요청_dto;
    protected CreateBidDto 첫입찰자가_시작가로_입찰_요청_dto;
    protected CreateBidDto 존재하지_않는_경매_아이디에_대한_입찰_요청_dto;
    protected CreateBidDto 존재하지_않는_사용자_아이디를_통한_입찰_요청_dto;
    protected CreateBidDto 종료된_경매에_대한_입찰_요청_dto;
    protected CreateBidDto 삭제된_경매에_대한_입찰_요청_dto;
    protected CreateBidDto 판매자가_본인_경매에_입찰_요청_dto;
    protected CreateBidDto 첫입찰시_시작가보다_낮은_입찰액으로_입찰_요청_dto;
    protected CreateBidDto 동일한_사용자가_입찰_요청_dto;
    protected CreateBidDto 이전_입찰액보다_낮은_입찰액으로_입찰_요청_dto;
    protected CreateBidDto 최소_입찰단위를_더한_금액보다_낮은_입찰액으로_입찰_요청_dto;
    protected static CreateBidDto 범위_밖의_금액으로_입찰_요청_dto1;
    protected static CreateBidDto 범위_밖의_금액으로_입찰_요청_dto2;

    @BeforeEach
    void setUp() {
        final Region 서울특별시 = new Region("서울특별시");
        final Region 강남구 = new Region("강남구");
        final Region 역삼동 = new Region("역삼동");

        서울특별시.addSecondRegion(강남구);
        강남구.addThirdRegion(역삼동);

        regionRepository.save(서울특별시);

        final Category 가구_카테고리 = new Category("가구");
        final Category 가구_서브_의자_카테고리 = new Category("의자");

        가구_카테고리.addSubCategory(가구_서브_의자_카테고리);

        categoryRepository.save(가구_카테고리);

        final User 판매자 = User.builder()
                             .name("판매자")
                             .profileImage(new ProfileImage("upload.png", "store.png"))
                             .reliability(new Reliability(4.7d))
                             .oauthId("12345")
                             .build();
        입찰자1 = User.builder()
                   .name("입찰자1")
                   .profileImage(new ProfileImage("upload.png", "store.png"))
                   .reliability(new Reliability(4.7d))
                   .oauthId("12346")
                   .build();
        입찰자2 = User.builder()
                   .name("입찰자2")
                   .profileImage(new ProfileImage("upload.png", "store.png"))
                   .reliability(new Reliability(4.7d))
                   .oauthId("78910")
                   .build();
        final AuctionImage 경매_이미지1 = new AuctionImage("auction_image.png", "auction_image.png");
        경매1 = Auction.builder()
                     .seller(판매자)
                     .title("경매 상품 1")
                     .description("이것은 경매 상품 1 입니다.")
                     .bidUnit(new BidUnit(1_000))
                     .startPrice(new Price(1_000))
                     .closingTime(LocalDateTime.now().plusDays(7))
                     .subCategory(가구_서브_의자_카테고리)
                     .build();
        final AuctionImage 경매_이미지2 = new AuctionImage("auction_image.png", "auction_image.png");
        final Auction 경매2 = Auction.builder()
                                   .seller(판매자)
                                   .title("경매 상품 2")
                                   .description("이것은 경매 상품 2 입니다.")
                                   .bidUnit(new BidUnit(1_000))
                                   .startPrice(new Price(1_000))
                                   .closingTime(LocalDateTime.now().plusDays(7))
                                   .subCategory(가구_서브_의자_카테고리)
                                   .build();
        final AuctionImage 경매_이미지3 = new AuctionImage("auction_image.png", "auction_image.png");
        final Auction 경매3 = Auction.builder()
                                   .seller(판매자)
                                   .title("경매 상품 2")
                                   .description("이것은 경매 상품 2 입니다.")
                                   .bidUnit(new BidUnit(1_000))
                                   .startPrice(new Price(1_000))
                                   .closingTime(LocalDateTime.now().plusDays(7))
                                   .subCategory(가구_서브_의자_카테고리)
                                   .build();
        입찰_내역이_없는_경매 = 경매3;
        입찰_내역이_하나_있던_경매 = 경매2;
        final Auction 종료된_경매 = Auction.builder()
                                      .seller(판매자)
                                      .title("경매 상품 2")
                                      .description("이것은 경매 상품 2 입니다.")
                                      .bidUnit(new BidUnit(1_000))
                                      .startPrice(new Price(1_000))
                                      .closingTime(LocalDateTime.now().minusDays(7))
                                      .subCategory(가구_서브_의자_카테고리)
                                      .build();
        final Auction 삭제된_경매 = Auction.builder()
                                      .seller(판매자)
                                      .title("경매 상품 2")
                                      .description("이것은 경매 상품 2 입니다.")
                                      .bidUnit(new BidUnit(1_000))
                                      .startPrice(new Price(1_000))
                                      .closingTime(LocalDateTime.now().plusDays(7))
                                      .build();
        삭제된_경매.delete();

        userRepository.save(판매자);
        userRepository.save(입찰자1);
        userRepository.save(입찰자2);

        경매1.addAuctionImages(List.of(경매_이미지1));
        경매2.addAuctionImages(List.of(경매_이미지2));
        경매3.addAuctionImages(List.of(경매_이미지3));

        경매1.addAuctionRegions(List.of(new AuctionRegion(역삼동)));
        경매2.addAuctionRegions(List.of(new AuctionRegion(역삼동)));
        경매3.addAuctionRegions(List.of(new AuctionRegion(역삼동)));
        종료된_경매.addAuctionRegions(List.of(new AuctionRegion(역삼동)));

        auctionRepository.save(경매1);
        auctionRepository.save(경매2);
        auctionRepository.save(경매3);
        auctionRepository.save(입찰_내역이_하나_있던_경매);
        auctionRepository.save(종료된_경매);
        auctionRepository.save(삭제된_경매);

        final Bid bid1 = new Bid(경매1, 입찰자1, new BidPrice(1_000));
        final Bid bid2 = new Bid(경매2, 입찰자1, new BidPrice(1_000));
        final Bid bid3 = new Bid(경매1, 입찰자2, new BidPrice(10_000));
        bidRepository.save(bid1);
        bidRepository.save(bid2);
        bidRepository.save(bid3);

        경매1.updateLastBid(bid1);
        경매2.updateLastBid(bid2);
        경매1.updateLastBid(bid3);

        입찰_요청_dto = new CreateBidDto(경매3.getId(), 10_000, 입찰자1.getId());
        입찰_내역이_하나_존재하는_경매에_대한_입찰_요청_dto = new CreateBidDto(입찰_내역이_하나_있던_경매.getId(), 14_000, 입찰자2.getId());
        첫입찰자가_시작가로_입찰_요청_dto = new CreateBidDto(경매3.getId(), 1_000, 입찰자1.getId());
        존재하지_않는_경매_아이디에_대한_입찰_요청_dto = new CreateBidDto(존재하지_않는_경매_아이디, 10_000, 입찰자1.getId());
        존재하지_않는_사용자_아이디를_통한_입찰_요청_dto = new CreateBidDto(경매3.getId(), 10_000, 존재하지_않는_사용자_아이디);
        종료된_경매에_대한_입찰_요청_dto = new CreateBidDto(종료된_경매.getId(), 10_000, 입찰자1.getId());
        삭제된_경매에_대한_입찰_요청_dto = new CreateBidDto(삭제된_경매.getId(), 10_000, 입찰자1.getId());
        판매자가_본인_경매에_입찰_요청_dto = new CreateBidDto(경매3.getId(), 10_000, 판매자.getId());
        첫입찰시_시작가보다_낮은_입찰액으로_입찰_요청_dto = new CreateBidDto(경매3.getId(), 900, 입찰자1.getId());
        동일한_사용자가_입찰_요청_dto = new CreateBidDto(입찰_내역이_하나_있던_경매.getId(), 12_000, 입찰자1.getId());
        이전_입찰액보다_낮은_입찰액으로_입찰_요청_dto = new CreateBidDto(입찰_내역이_하나_있던_경매.getId(), 500, 입찰자2.getId());
        최소_입찰단위를_더한_금액보다_낮은_입찰액으로_입찰_요청_dto = new CreateBidDto(입찰_내역이_하나_있던_경매.getId(), 1_500, 입찰자2.getId());
        범위_밖의_금액으로_입찰_요청_dto1 = new CreateBidDto(경매3.getId(), -1, 입찰자2.getId());
        범위_밖의_금액으로_입찰_요청_dto2 = new CreateBidDto(경매3.getId(), 2_100_000_001, 입찰자2.getId());
    }
}
