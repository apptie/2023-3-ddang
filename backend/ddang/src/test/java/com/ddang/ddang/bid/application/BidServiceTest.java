package com.ddang.ddang.bid.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ddang.ddang.auction.application.exception.AuctionNotFoundException;
import com.ddang.ddang.auction.domain.Auction;
import com.ddang.ddang.auction.domain.BidUnit;
import com.ddang.ddang.auction.domain.Price;
import com.ddang.ddang.auction.infrastructure.persistence.JpaAuctionRepository;
import com.ddang.ddang.bid.application.dto.CreateBidDto;
import com.ddang.ddang.bid.application.dto.LoginUserDto;
import com.ddang.ddang.bid.application.dto.ReadBidDto;
import com.ddang.ddang.bid.application.exception.InvalidAuctionToBidException;
import com.ddang.ddang.bid.application.exception.InvalidBidPriceException;
import com.ddang.ddang.bid.application.exception.InvalidBidderException;
import com.ddang.ddang.bid.application.exception.UserNotFoundException;
import com.ddang.ddang.configuration.IsolateDatabase;
import com.ddang.ddang.user.domain.User;
import com.ddang.ddang.user.infrastructure.persistence.JpaUserRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;

@IsolateDatabase
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
class BidServiceTest {

    @Autowired
    BidService bidService;

    @Autowired
    JpaAuctionRepository auctionRepository;

    @Autowired
    JpaUserRepository userRepository;

    @Test
    void 입찰을_등록한다() {
        // given
        final User seller = User.builder()
                                .name("판매자")
                                .profileImage("profile.png")
                                .reliability(4.7d)
                                .oauthId("12345")
                                .build();
        final Auction auction = Auction.builder()
                                       .seller(seller)
                                       .title("경매 상품 1")
                                       .description("이것은 경매 상품 1 입니다.")
                                       .bidUnit(new BidUnit(1_000))
                                       .startPrice(new Price(1_000))
                                       .closingTime(LocalDateTime.now().plusDays(7))
                                       .build();
        final User user = User.builder()
                              .name("사용자")
                              .profileImage("profile.png")
                              .reliability(4.7d)
                              .oauthId("12346")
                              .build();

        userRepository.save(seller);
        auctionRepository.save(auction);
        userRepository.save(user);

        final LoginUserDto loginUserDto = new LoginUserDto(user.getId());
        final CreateBidDto createBidDto = new CreateBidDto(auction.getId(), 10_000);

        // when
        final Long actual = bidService.create(loginUserDto, createBidDto);

        // then
        SoftAssertions.assertSoftly(softAssertions -> {
            softAssertions.assertThat(actual).isPositive();
            softAssertions.assertThat(auction.getLastBid().getPrice().getValue()).isEqualTo(createBidDto.bidPrice());
            softAssertions.assertThat(auction.getAuctioneerCount()).isEqualTo(1);
        });
    }

    @Test
    void 마지막_입찰자와_다른_사람은_마지막_입찰액과_최소_입찰단위를_더한_금액_이상의_금액으로_입찰을_등록할_수_있다() {
        // given
        final User seller = User.builder()
                                .name("판매자")
                                .profileImage("profile.png")
                                .reliability(4.7d)
                                .oauthId("12345")
                                .build();
        final Auction auction = Auction.builder()
                                       .seller(seller)
                                       .title("경매 상품 1")
                                       .description("이것은 경매 상품 1 입니다.")
                                       .bidUnit(new BidUnit(1_000))
                                       .startPrice(new Price(1_000))
                                       .closingTime(LocalDateTime.now().plusDays(7))
                                       .build();
        final User user1 = User.builder()
                               .name("사용자1")
                               .profileImage("profile.png")
                               .reliability(4.7d)
                               .oauthId("12346")
                               .build();
        final User user2 = User.builder()
                               .name("사용자2")
                               .profileImage("profile.png")
                               .reliability(4.7d)
                               .oauthId("12347")
                               .build();

        userRepository.save(seller);
        auctionRepository.save(auction);
        userRepository.save(user1);
        userRepository.save(user2);

        final LoginUserDto loginUserDto1 = new LoginUserDto(user1.getId());
        final LoginUserDto loginUserDto2 = new LoginUserDto(user2.getId());
        final CreateBidDto createBidDto1 = new CreateBidDto(auction.getId(), 10_000);
        final CreateBidDto createBidDto2 = new CreateBidDto(auction.getId(), 14_000);

        bidService.create(loginUserDto1, createBidDto1);

        // when
        final Long actual = bidService.create(loginUserDto2, createBidDto2);

        // then
        assertThat(actual).isPositive();
    }

    @Test
    void 첫_입찰자는_시작가를_입찰로_등록할_수_있다() {
        // given
        final User seller = User.builder()
                                .name("판매자")
                                .profileImage("profile.png")
                                .reliability(4.7d)
                                .oauthId("12345")
                                .build();
        final Auction auction = Auction.builder()
                                       .seller(seller)
                                       .title("경매 상품 1")
                                       .description("이것은 경매 상품 1 입니다.")
                                       .bidUnit(new BidUnit(1_000))
                                       .startPrice(new Price(1_000))
                                       .closingTime(LocalDateTime.now().plusDays(7))
                                       .build();
        final User buyer = User.builder()
                               .name("사용자2")
                               .profileImage("profile.png")
                               .reliability(4.7d)
                               .oauthId("12346")
                               .build();

        userRepository.save(seller);
        auctionRepository.save(auction);
        userRepository.save(buyer);

        final LoginUserDto loginUserDto = new LoginUserDto(buyer.getId());
        final CreateBidDto createBidDto = new CreateBidDto(auction.getId(), 1_000);

        // when
        final Long actual = bidService.create(loginUserDto, createBidDto);

        // then
        assertThat(actual).isPositive();
    }

    @Test
    void 존재하지_않는_경매에_입찰하는_경우_예외가_발생한다() {
        // given
        final Long invaliAuctionId = -9999L;

        final User user = User.builder()
                              .name("사용자1")
                              .profileImage("profile.png")
                              .reliability(4.7d)
                              .oauthId("12345")
                              .build();
        userRepository.save(user);

        final LoginUserDto loginUserDto = new LoginUserDto(user.getId());
        final CreateBidDto createBidDto = new CreateBidDto(invaliAuctionId, 10_000);

        // when & then
        assertThatThrownBy(() -> bidService.create(loginUserDto, createBidDto))
                .isInstanceOf(AuctionNotFoundException.class)
                .hasMessage("해당 경매를 찾을 수 없습니다.");
    }

    @Test
    void 존재하지_않는_사용자가_경매에_입찰하는_경우_예외가_발생한다() {
        // given
        final Long invalidUserId = -9999L;

        final Auction auction = Auction.builder()
                                       .title("경매 상품 1")
                                       .description("이것은 경매 상품 1 입니다.")
                                       .bidUnit(new BidUnit(1_000))
                                       .startPrice(new Price(1_000))
                                       .closingTime(LocalDateTime.now().plusDays(7))
                                       .build();

        auctionRepository.save(auction);

        final LoginUserDto loginUserDto = new LoginUserDto(invalidUserId);
        final CreateBidDto createBidDto = new CreateBidDto(auction.getId(), 10_000);

        // when & then
        assertThatThrownBy(() -> bidService.create(loginUserDto, createBidDto))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("해당 사용자를 찾을 수 없습니다.");
    }

    @Test
    void 종료된_경매에_입찰하는_경우_예외가_발생한다() {
        // given
        final Auction auction = Auction.builder()
                                       .title("경매 상품 1")
                                       .description("이것은 경매 상품 1 입니다.")
                                       .bidUnit(new BidUnit(1_000))
                                       .startPrice(new Price(1_000))
                                       .closingTime(LocalDateTime.now().minusDays(1))
                                       .build();
        final User user = User.builder()
                              .name("사용자1")
                              .profileImage("profile.png")
                              .reliability(4.7d)
                              .oauthId("12345")
                              .build();

        auctionRepository.save(auction);
        userRepository.save(user);

        final LoginUserDto loginUserDto = new LoginUserDto(user.getId());
        final CreateBidDto createBidDto = new CreateBidDto(auction.getId(), 10_000);

        // when & then
        assertThatThrownBy(() -> bidService.create(loginUserDto, createBidDto))
                .isInstanceOf(InvalidAuctionToBidException.class)
                .hasMessage("이미 종료된 경매입니다");
    }

    @Test
    void 삭제된_경매에_입찰하는_경우_예외가_발생한다() {
        // given
        final Auction auction = Auction.builder()
                                       .title("경매 상품 1")
                                       .description("이것은 경매 상품 1 입니다.")
                                       .bidUnit(new BidUnit(1_000))
                                       .startPrice(new Price(1_000))
                                       .closingTime(LocalDateTime.now().plusDays(7))
                                       .build();
        final User user = User.builder()
                              .name("사용자1")
                              .profileImage("profile.png")
                              .reliability(4.7d)
                              .oauthId("12345")
                              .build();

        auctionRepository.save(auction);
        userRepository.save(user);

        final LoginUserDto loginUserDto = new LoginUserDto(user.getId());
        final CreateBidDto createBidDto = new CreateBidDto(auction.getId(), 10_000);
        auction.delete();

        // when & then
        assertThatThrownBy(() -> bidService.create(loginUserDto, createBidDto))
                .isInstanceOf(InvalidAuctionToBidException.class)
                .hasMessage("삭제된 경매입니다");
    }

    @Test
    void 판매자가_입찰하는_경우_예외가_발생한다() {
        final User user = User.builder()
                              .name("사용자1")
                              .profileImage("profile.png")
                              .reliability(4.7d)
                              .oauthId("12345")
                              .build();
        final Auction auction = Auction.builder()
                                       .seller(user)
                                       .title("경매 상품 1")
                                       .description("이것은 경매 상품 1 입니다.")
                                       .bidUnit(new BidUnit(1_000))
                                       .startPrice(new Price(1_000))
                                       .closingTime(LocalDateTime.now().plusDays(7))
                                       .build();

        userRepository.save(user);
        auctionRepository.save(auction);

        final LoginUserDto loginUserDto = new LoginUserDto(user.getId());
        final CreateBidDto createBidDto = new CreateBidDto(auction.getId(), 10_000);

        // when && then
        assertThatThrownBy(() -> bidService.create(loginUserDto, createBidDto))
                .isInstanceOf(InvalidBidderException.class)
                .hasMessage("판매자는 입찰할 수 없습니다");
    }

    @Test
    void 첫_입찰자가_시작가_낮은_금액으로_입찰하는_경우_예외가_발생한다() {
        // given
        final User seller = User.builder()
                                .name("판매자")
                                .profileImage("profile.png")
                                .reliability(4.7d)
                                .oauthId("12345")
                                .build();
        final Auction auction = Auction.builder()
                                       .seller(seller)
                                       .title("경매 상품 1")
                                       .description("이것은 경매 상품 1 입니다.")
                                       .bidUnit(new BidUnit(1_000))
                                       .startPrice(new Price(1_000))
                                       .closingTime(LocalDateTime.now().plusDays(7))
                                       .build();
        final User user = User.builder()
                              .name("사용자1")
                              .profileImage("profile.png")
                              .reliability(4.7d)
                              .oauthId("12346")
                              .build();

        userRepository.save(seller);
        auctionRepository.save(auction);
        userRepository.save(user);

        final LoginUserDto loginUserDto = new LoginUserDto(user.getId());
        final CreateBidDto createBidDto = new CreateBidDto(auction.getId(), 900);

        // when && then
        assertThatThrownBy(() -> bidService.create(loginUserDto, createBidDto))
                .isInstanceOf(InvalidBidPriceException.class)
                .hasMessage("입찰 금액이 잘못되었습니다");
    }

    @Test
    void 마지막_입찰자가_연속으로_입찰하는_경우_예외가_발생한다() {
        // given
        final User seller = User.builder()
                                .name("판매자")
                                .profileImage("profile.png")
                                .reliability(4.7d)
                                .oauthId("12345")
                                .build();
        final Auction auction = Auction.builder()
                                       .seller(seller)
                                       .title("경매 상품 1")
                                       .description("이것은 경매 상품 1 입니다.")
                                       .bidUnit(new BidUnit(1_000))
                                       .startPrice(new Price(1_000))
                                       .closingTime(LocalDateTime.now().plusDays(7))
                                       .build();
        final User user = User.builder()
                              .name("사용자1")
                              .profileImage("profile.png")
                              .reliability(4.7d)
                              .oauthId("12346")
                              .build();

        userRepository.save(seller);
        auctionRepository.save(auction);
        userRepository.save(user);

        final LoginUserDto loginUserDto1 = new LoginUserDto(user.getId());
        final CreateBidDto createBidDto1 = new CreateBidDto(auction.getId(), 10_000);
        bidService.create(loginUserDto1, createBidDto1);

        final LoginUserDto loginUserDto2 = new LoginUserDto(user.getId());
        final CreateBidDto createBidDto2 = new CreateBidDto(auction.getId(), 12_000);

        // when && then
        assertThatThrownBy(() -> bidService.create(loginUserDto2, createBidDto2))
                .isInstanceOf(InvalidBidderException.class)
                .hasMessage("이미 최고 입찰자입니다");
    }

    @Test
    void 마지막_입찰액보다_낮은_금액으로_입찰하는_경우_예외가_발생한다() {
        // given
        final User seller = User.builder()
                                .name("판매자")
                                .profileImage("profile.png")
                                .reliability(4.7d)
                                .oauthId("12345")
                                .build();
        final Auction auction = Auction.builder()
                                       .seller(seller)
                                       .title("경매 상품 1")
                                       .description("이것은 경매 상품 1 입니다.")
                                       .bidUnit(new BidUnit(1_000))
                                       .startPrice(new Price(1_000))
                                       .closingTime(LocalDateTime.now().plusDays(7))
                                       .build();
        final User user1 = User.builder()
                               .name("사용자1")
                               .profileImage("profile.png")
                               .reliability(4.7d)
                               .oauthId("12346")
                               .build();
        final User user2 = User.builder()
                               .name("사용자2")
                               .profileImage("profile.png")
                               .reliability(4.7d)
                               .oauthId("12347")
                               .build();

        userRepository.save(seller);
        auctionRepository.save(auction);
        userRepository.save(user1);
        userRepository.save(user2);

        final LoginUserDto loginUserDto1 = new LoginUserDto(user1.getId());
        final CreateBidDto createBidDto1 = new CreateBidDto(auction.getId(), 10_000);
        bidService.create(loginUserDto1, createBidDto1);

        final LoginUserDto loginUserDto2 = new LoginUserDto(user2.getId());
        final CreateBidDto createBidDto2 = new CreateBidDto(auction.getId(), 8_000);

        // when & then
        assertThatThrownBy(() -> bidService.create(loginUserDto2, createBidDto2))
                .isInstanceOf(InvalidBidPriceException.class)
                .hasMessage("가능 입찰액보다 낮은 금액을 입력했습니다");
    }

    @Test
    void 최소_입찰_단위보다_낮은_금액으로_입찰하는_경우_예외가_발생한다() {
        // given
        final User seller = User.builder()
                                .name("판매자")
                                .profileImage("profile.png")
                                .reliability(4.7d)
                                .oauthId("12345")
                                .build();
        final Auction auction = Auction.builder()
                                       .seller(seller)
                                       .title("경매 상품 1")
                                       .description("이것은 경매 상품 1 입니다.")
                                       .bidUnit(new BidUnit(1_000))
                                       .startPrice(new Price(1_000))
                                       .closingTime(LocalDateTime.now().plusDays(7))
                                       .build();
        final User user1 = User.builder()
                               .name("사용자1")
                               .profileImage("profile.png")
                               .reliability(4.7d)
                               .oauthId("12346")
                               .build();
        final User user2 = User.builder()
                               .name("사용자2")
                               .profileImage("profile.png")
                               .reliability(4.7d)
                               .oauthId("12347")
                               .build();

        userRepository.save(seller);
        auctionRepository.save(auction);
        userRepository.save(user1);
        userRepository.save(user2);

        final LoginUserDto loginUserDto1 = new LoginUserDto(user1.getId());
        final CreateBidDto createBidDto1 = new CreateBidDto(auction.getId(), 10_000);
        bidService.create(loginUserDto1, createBidDto1);

        final LoginUserDto loginUserDto2 = new LoginUserDto(user2.getId());
        final CreateBidDto createBidDto2 = new CreateBidDto(auction.getId(), 10_500);

        // when & then
        assertThatThrownBy(() -> bidService.create(loginUserDto2, createBidDto2))
                .isInstanceOf(InvalidBidPriceException.class)
                .hasMessage("가능 입찰액보다 낮은 금액을 입력했습니다");
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 2_100_000_001})
    void 범위_밖의_금액으로_입찰하는_경우_예외가_발생한다(final int bidPrice) {
        // given
        final Auction auction = Auction.builder()
                                       .title("경매 상품 1")
                                       .description("이것은 경매 상품 1 입니다.")
                                       .bidUnit(new BidUnit(1_000))
                                       .startPrice(new Price(1_000))
                                       .closingTime(LocalDateTime.now().plusDays(7))
                                       .build();
        final User user = User.builder()
                              .name("사용자1")
                              .profileImage("profile.png")
                              .reliability(4.7d)
                              .oauthId("12345")
                              .build();

        auctionRepository.save(auction);
        userRepository.save(user);

        final LoginUserDto loginUserDto = new LoginUserDto(user.getId());
        final CreateBidDto createBidDto = new CreateBidDto(auction.getId(), bidPrice);

        // when & then
        assertThatThrownBy(() -> bidService.create(loginUserDto, createBidDto))
                .isInstanceOf(InvalidBidPriceException.class)
                .hasMessage("입찰 금액이 잘못되었습니다");
    }

    @Test
    void 특정_경매에_대한_입찰_목록을_조회한다() {
        // given
        final User seller = User.builder()
                                .name("판매자")
                                .profileImage("profile.png")
                                .reliability(4.7d)
                                .oauthId("12345")
                                .build();
        final Auction auction1 = Auction.builder()
                                        .seller(seller)
                                        .title("경매 상품 1")
                                        .description("이것은 경매 상품 1 입니다.")
                                        .bidUnit(new BidUnit(1_000))
                                        .startPrice(new Price(1_000))
                                        .closingTime(LocalDateTime.now().plusDays(7))
                                        .build();
        final Auction auction2 = Auction.builder()
                                        .seller(seller)
                                        .title("경매 상품 2")
                                        .description("이것은 경매 상품 2 입니다.")
                                        .bidUnit(new BidUnit(1_000))
                                        .startPrice(new Price(1_000))
                                        .closingTime(LocalDateTime.now().plusDays(7))
                                        .build();
        final User user1 = User.builder()
                               .name("사용자1")
                               .profileImage("profile.png")
                               .reliability(4.7d)
                               .oauthId("12346")
                               .build();
        final User user2 = User.builder()
                               .name("사용자2")
                               .profileImage("profile.png")
                               .reliability(4.7d)
                               .oauthId("12347")
                               .build();

        userRepository.save(seller);
        auctionRepository.save(auction1);
        auctionRepository.save(auction2);
        userRepository.save(user1);
        userRepository.save(user2);

        final LoginUserDto loginUserDto1 = new LoginUserDto(user1.getId());
        final CreateBidDto createBidDto1 = new CreateBidDto(auction1.getId(), 1_000);
        bidService.create(loginUserDto1, createBidDto1);

        final LoginUserDto loginUserDto2 = new LoginUserDto(user1.getId());
        final CreateBidDto createBidDto2 = new CreateBidDto(auction2.getId(), 1_000);
        bidService.create(loginUserDto2, createBidDto2);

        final LoginUserDto loginUserDto3 = new LoginUserDto(user2.getId());
        final CreateBidDto createBidDto3 = new CreateBidDto(auction1.getId(), 10_000);
        bidService.create(loginUserDto3, createBidDto3);

        // when
        final List<ReadBidDto> actual = bidService.readAllByAuctionId(auction1.getId());

        // then
        SoftAssertions.assertSoftly(softAssertions -> {
            softAssertions.assertThat(actual.get(0).name()).isEqualTo(user1.getName());
            softAssertions.assertThat(actual.get(1).name()).isEqualTo(user2.getName());
        });
    }

    @Test
    void 특정_경매에_대한_입찰_내역이_없다면_빈배열을_반환한다() {
        // given
        final Auction auction1 = Auction.builder()
                                        .title("경매 상품 1")
                                        .description("이것은 경매 상품 1 입니다.")
                                        .bidUnit(new BidUnit(1_000))
                                        .startPrice(new Price(1_000))
                                        .closingTime(LocalDateTime.now().plusDays(7))
                                        .build();
        final User user1 = User.builder()
                               .name("사용자1")
                               .profileImage("profile.png")
                               .reliability(4.7d)
                               .oauthId("12345")
                               .build();

        auctionRepository.save(auction1);
        userRepository.save(user1);

        // when
        final List<ReadBidDto> actual = bidService.readAllByAuctionId(auction1.getId());

        // then
        assertThat(actual).isEmpty();
    }

    @Test
    void 입찰을_조회하려는_경매가_존재하지_않는_경우_예외를_반환한다() {
        // given
        final Long invalidAuctionId = -999L;

        // when & then
        assertThatThrownBy(() -> bidService.readAllByAuctionId(invalidAuctionId))
                .isInstanceOf(AuctionNotFoundException.class)
                .hasMessage("해당 경매를 찾을 수 없습니다.");
    }
}