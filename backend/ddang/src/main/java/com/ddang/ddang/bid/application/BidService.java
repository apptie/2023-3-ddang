package com.ddang.ddang.bid.application;

import com.ddang.ddang.auction.domain.Auction;
import com.ddang.ddang.auction.domain.repository.AuctionRepository;
import com.ddang.ddang.auction.infrastructure.persistence.exception.AuctionNotFoundException;
import com.ddang.ddang.bid.application.dto.request.CreateBidDto;
import com.ddang.ddang.bid.application.dto.response.ReadBidDto;
import com.ddang.ddang.bid.application.event.BidNotificationEvent;
import com.ddang.ddang.bid.application.exception.BiddingSellerException;
import com.ddang.ddang.bid.application.exception.BiddingWinnerException;
import com.ddang.ddang.bid.application.exception.InvalidAuctionToBidException;
import com.ddang.ddang.bid.application.exception.LessThanPreviousBidException;
import com.ddang.ddang.bid.application.exception.LessThanStartPriceException;
import com.ddang.ddang.bid.domain.Bid;
import com.ddang.ddang.bid.domain.BidPrice;
import com.ddang.ddang.bid.domain.repository.BidRepository;
import com.ddang.ddang.user.domain.User;
import com.ddang.ddang.user.domain.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BidService {

    private final ApplicationEventPublisher bidEventPublisher;
    private final AuctionRepository auctionRepository;
    private final UserRepository userRepository;
    private final BidRepository bidRepository;

    @Transactional
    public Long create(final CreateBidDto bidDto, final String auctionImageAbsoluteUrl) {
        final User bidder = userRepository.getByIdOrThrow(bidDto.userId());
        final Auction auction = auctionRepository.getTotalAuctionByIdOrThrow(bidDto.auctionId());

        checkInvalidAuction(auction);
        checkInvalidBid(auction, bidder, bidDto);

        auction.findLastBidder()
               .ifPresent(previousBidder ->
                       publishBidNotificationEvent(auctionImageAbsoluteUrl, auction, previousBidder));

        return saveAndUpdateLastBid(bidDto, auction, bidder).getId();
    }

    private void publishBidNotificationEvent(
            final String auctionImageAbsoluteUrl,
            final Auction auction,
            final User previousBidder
    ) {
        final BidNotificationEvent notificationEvent = new BidNotificationEvent(
                previousBidder.getId(),
                auction,
                auctionImageAbsoluteUrl
        );

        bidEventPublisher.publishEvent(notificationEvent);
    }

    private void checkInvalidAuction(final Auction auction) {
        final LocalDateTime now = LocalDateTime.now();

        if (auction.isClosed(now)) {
            throw new InvalidAuctionToBidException("이미 종료된 경매입니다");
        }
    }

    private void checkInvalidBid(final Auction auction, final User bidder, final CreateBidDto bidDto) {
        checkIsSeller(auction, bidder);

        final BidPrice bidPrice = processBidPrice(bidDto.bidPrice());

        auction.findLastBid()
                .ifPresentOrElse(
                        lastBid -> {
                            checkIsNotLastBidder(lastBid, bidder);
                            checkInvalidBidPrice(lastBid, bidPrice);
                        },
                        () -> checkInvalidFirstBidPrice(auction, bidPrice)
                );
    }

    private BidPrice processBidPrice(final int value) {
        return new BidPrice(value);
    }

    private void checkIsSeller(final Auction auction, final User bidder) {
        if (auction.isOwner(bidder)) {
            throw new BiddingSellerException("판매자는 입찰할 수 없습니다");
        }
    }

    private void checkInvalidFirstBidPrice(final Auction auction, final BidPrice bidPrice) {
        if (auction.isInvalidFirstBidPrice(bidPrice)) {
            throw new LessThanStartPriceException("시작 입찰가보다 낮은 금액을 입력했습니다.");
        }
    }

    private void checkIsNotLastBidder(final Bid lastBid, final User bidder) {
        if (lastBid.isSameBidder(bidder)) {
            throw new BiddingWinnerException("이미 최고 입찰자입니다");
        }
    }

    private void checkInvalidBidPrice(final Bid lastBid, final BidPrice bidPrice) {
        if (lastBid.isNextBidPriceGreaterThan(bidPrice)) {
            throw new LessThanPreviousBidException("이전 입찰가보다 낮은 금액을 입력했습니다.");
        }
    }

    private Bid saveAndUpdateLastBid(final CreateBidDto bidDto, final Auction auction, final User bidder) {
        final Bid createBid = bidDto.toEntity(auction, bidder);
        final Bid saveBid = bidRepository.save(createBid);

        auction.updateLastBid(saveBid);

        return saveBid;
    }

    public List<ReadBidDto> readAllByAuctionId(final Long auctionId) {
        if (auctionRepository.existsById(auctionId)) {
            final List<Bid> bids = bidRepository.findAllByAuctionId(auctionId);

            return bids.stream()
                       .map(ReadBidDto::from)
                       .toList();
        }

        throw new AuctionNotFoundException("해당 경매를 찾을 수 없습니다.");
    }
}
