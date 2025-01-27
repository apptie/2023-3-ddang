package com.ddang.ddang.bid.infrastructure.persistence;

import com.ddang.ddang.bid.domain.Bid;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface JpaBidRepository extends JpaRepository<Bid, Long> {

    @Query("""
        SELECT b
        FROM Bid b
        JOIN FETCH b.bidder bi
        LEFT JOIN FETCH bi.profileImage
        WHERE b.auction.id = :auctionId
        ORDER BY b.id ASC
    """)
    List<Bid> findAllByAuctionIdOrderByIdAsc(final Long auctionId);

    @Query("SELECT b FROM Bid b WHERE b.auction.id = :auctionId ORDER BY b.id DESC LIMIT 1")
    Optional<Bid> findLastBidByAuctionId(@Param("auctionId") final Long auctionId);
}
