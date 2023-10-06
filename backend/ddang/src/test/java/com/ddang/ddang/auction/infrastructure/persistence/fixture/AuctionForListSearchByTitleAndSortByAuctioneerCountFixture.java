package com.ddang.ddang.auction.infrastructure.persistence.fixture;

import com.ddang.ddang.auction.domain.Auction;
import com.ddang.ddang.auction.presentation.dto.request.ReadAuctionSearchCondition;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;

@SuppressWarnings("NonAsciiCharacters")
public class AuctionForListSearchByTitleAndSortByAuctioneerCountFixture extends QuerydslAuctionRepositoryImplForListFixture {

    protected Sort 참여_인원순_정렬 = Sort.by(Order.asc("auctioneerCount"));
    protected ReadAuctionSearchCondition 검색어_맥북 = new ReadAuctionSearchCondition("맥북");

    protected Auction 첫번째_페이지_인덱스_0_참여자_7_id_4;
    protected Auction 첫번째_페이지_인덱스_1_참여자_6_id_16;
    protected Auction 첫번째_페이지_인덱스_2_참여자_6_id_15;
    protected Auction 두번째_페이지_인덱스_0_참여자_6_id_14;
    protected Auction 두번째_페이지_인덱스_1_참여자_6_id_13;
    protected Auction 두번째_페이지_인덱스_2_참여자_6_id_12;
    protected Auction 세번째_페이지_인덱스_0_참여자_6_id_11;
    protected Auction 세번째_페이지_인덱스_1_참여자_6_id_10;
    protected Auction 세번째_페이지_인덱스_2_참여자_6_id_9;
    protected Auction 네번째_페이지_인덱스_0_참여자_6_id_8;
    protected Auction 네번째_페이지_인덱스_1_참여자_4_id_3;
    protected Auction 네번째_페이지_인덱스_2_참여자_3_id_7;
    protected Auction 다섯번째_페이지_인덱스_0_참여자_2_id_1;
    protected Auction 다섯번째_페이지_인덱스_1_참여자_1_id_2;

    @BeforeEach
    void fixtureSetUp() {
        첫번째_페이지_인덱스_0_참여자_7_id_4 = 경매4;
        첫번째_페이지_인덱스_1_참여자_6_id_16 = 경매16;
        첫번째_페이지_인덱스_2_참여자_6_id_15 = 경매15;
        두번째_페이지_인덱스_0_참여자_6_id_14 = 경매14;
        두번째_페이지_인덱스_1_참여자_6_id_13 = 경매13;
        두번째_페이지_인덱스_2_참여자_6_id_12 = 경매12;
        세번째_페이지_인덱스_0_참여자_6_id_11 = 경매11;
        세번째_페이지_인덱스_1_참여자_6_id_10 = 경매10;
        세번째_페이지_인덱스_2_참여자_6_id_9 = 경매9;
        네번째_페이지_인덱스_0_참여자_6_id_8 = 경매8;
        네번째_페이지_인덱스_1_참여자_4_id_3 = 경매3;
        네번째_페이지_인덱스_2_참여자_3_id_7 = 경매7;
        다섯번째_페이지_인덱스_0_참여자_2_id_1 = 경매1;
        다섯번째_페이지_인덱스_1_참여자_1_id_2 = 경매2;
    }
}