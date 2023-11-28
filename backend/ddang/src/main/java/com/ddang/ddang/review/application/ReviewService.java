package com.ddang.ddang.review.application;

import com.ddang.ddang.auction.domain.Auction;
import com.ddang.ddang.auction.domain.repository.AuctionRepository;
import com.ddang.ddang.review.application.dto.request.CreateReviewDto;
import com.ddang.ddang.review.application.dto.response.ReadSingleReviewDto;
import com.ddang.ddang.review.application.dto.response.ReadMultipleReviewDto;
import com.ddang.ddang.review.application.exception.AlreadyReviewException;
import com.ddang.ddang.review.application.exception.InvalidUserToReview;
import com.ddang.ddang.review.application.exception.RevieweeNotFoundException;
import com.ddang.ddang.review.application.exception.ReviewerNotFoundException;
import com.ddang.ddang.review.domain.Review;
import com.ddang.ddang.review.domain.repository.ReviewRepository;
import com.ddang.ddang.user.domain.User;
import com.ddang.ddang.user.domain.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final AuctionRepository auctionRepository;
    private final UserRepository userRepository;

    @Transactional
    public Long create(final CreateReviewDto reviewDto) {
        final Auction findAuction = auctionRepository.getTotalAuctionByIdOrThrow(reviewDto.auctionId());
        final User writer = userRepository.findById(reviewDto.writerId())
                                          .orElseThrow(() ->
                                                  new ReviewerNotFoundException("리뷰어 정보를 찾을 수 없습니다.")
                                          );
        final User target = userRepository.findById(reviewDto.targetId())
                                          .orElseThrow(() ->
                                                  new RevieweeNotFoundException("평가 상대의 정보를 찾을 수 없습니다.")
                                          );

        validateWriterCanReview(findAuction, writer);

        final Review review = reviewDto.toEntity(findAuction, writer, target);

        return reviewRepository.save(review)
                               .getId();
    }

    private void validateWriterCanReview(final Auction auction, final User writer) {
        if (!auction.isSellerOrWinner(writer, LocalDateTime.now())) {
            throw new InvalidUserToReview("경매의 판매자 또는 최종 낙찰자만 평가가 가능합니다.");
        }

        validateAlreadyReviewed(auction, writer);
    }

    private void validateAlreadyReviewed(final Auction auction, final User writer) {
        if (reviewRepository.existsByAuctionIdAndWriterId(auction.getId(), writer.getId())) {
            throw new AlreadyReviewException("이미 평가하였습니다.");
        }
    }

    public ReadSingleReviewDto readByReviewId(final Long reviewId) {
        final Review findReview = reviewRepository.getByIdOrThrow(reviewId);

        return ReadSingleReviewDto.from(findReview);
    }

    public List<ReadMultipleReviewDto> readAllByTargetId(final Long targetId) {
        final List<Review> targetReviews = reviewRepository.findAllByTargetId(targetId);

        return targetReviews.stream()
                            .map(ReadMultipleReviewDto::from)
                            .toList();
    }

    public ReadSingleReviewDto readByAuctionIdAndWriterId(final Long writerId, final Long auctionId) {
        return reviewRepository.findByAuctionIdAndWriterId(auctionId, writerId)
                               .map(ReadSingleReviewDto::from)
                               .orElse(ReadSingleReviewDto.EMPTY);
    }
}
