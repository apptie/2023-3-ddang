package com.ddang.ddang.auction.presentation.fixture;

import com.ddang.ddang.authentication.infrastructure.jwt.PrivateClaims;
import com.ddang.ddang.configuration.CommonControllerSliceTest;
import com.ddang.ddang.review.application.dto.response.ReadMultipleReviewDto.ReviewerInfoDto;
import com.ddang.ddang.review.application.dto.response.ReadSingleReviewDto;
import com.ddang.ddang.review.application.dto.response.ReadMultipleReviewDto;

import java.time.LocalDateTime;

@SuppressWarnings("NonAsciiCharacters")
public class AuctionReviewControllerFixture extends CommonControllerSliceTest {

    protected Long 유효한_평가_작성자_아이디 = 1L;
    protected String 액세스_토큰 = "Bearer accessToken";
    protected PrivateClaims 유효한_작성자_비공개_클레임 = new PrivateClaims(유효한_평가_작성자_아이디);
    protected Long 유효한_경매_아이디 = 1L;
    protected String 판매자_프로필_이미지_이름 = "store-name.png";
    protected ReviewerInfoDto 판매자 = new ReviewerInfoDto(1L, "판매자", 판매자_프로필_이미지_이름, 5.0d, "12347");
    protected Long 구매자가_판매자에게_받은_평가_아이디 = 1L;
    protected ReadMultipleReviewDto 구매자가_판매자1에게_받은_평가 =
            new ReadMultipleReviewDto(구매자가_판매자에게_받은_평가_아이디, 판매자, "친절하다.", 5.0d, LocalDateTime.now());
    protected ReadSingleReviewDto 구매자가_판매자1에게_받은_평가_내용 = new ReadSingleReviewDto(
            구매자가_판매자1에게_받은_평가.score(),
            구매자가_판매자1에게_받은_평가.content()
    );
}
