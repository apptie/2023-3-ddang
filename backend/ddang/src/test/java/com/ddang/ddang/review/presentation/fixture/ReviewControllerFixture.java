package com.ddang.ddang.review.presentation.fixture;

import com.ddang.ddang.authentication.infrastructure.jwt.PrivateClaims;
import com.ddang.ddang.configuration.CommonControllerSliceTest;
import com.ddang.ddang.review.application.dto.response.ReadMultipleReviewDto.ReviewerInfoDto;
import com.ddang.ddang.review.application.dto.response.ReadSingleReviewDto;
import com.ddang.ddang.review.application.dto.response.ReadMultipleReviewDto;
import com.ddang.ddang.review.presentation.dto.request.CreateReviewRequest;

import java.time.LocalDateTime;

@SuppressWarnings("NonAsciiCharacters")
public class ReviewControllerFixture extends CommonControllerSliceTest {

    private Long 유효한_평가_작성자_아이디 = 1L;
    private Long 유효한_평가_대상_아이디 = 2L;
    private Long 유효한_경매_아이디 = 1L;
    private Long 사용자가_이미_평가한_경매_아이디 = 2L;
    private String 판매자1_프로필_이미지_이름 = "store-name.png";
    private String 판매자2_프로필_이미지_이름 = "store-name.png";
    private String 구매자_프로필_이미지_이름 = "store-name.png";
    private ReviewerInfoDto 판매자1 = new ReviewerInfoDto(1L, "판매자1", 판매자1_프로필_이미지_이름, 5.0d, "12347");
    private ReviewerInfoDto 판매자2 = new ReviewerInfoDto(2L, "판매자2", 판매자2_프로필_이미지_이름, 5.0d, "12348");
    protected ReviewerInfoDto 구매자 = new ReviewerInfoDto(3L, "구매자", 구매자_프로필_이미지_이름, 5.0d, "12349");
    protected String 액세스_토큰 = "Bearer accessToken";
    protected PrivateClaims 유효한_작성자_비공개_클레임 = new PrivateClaims(유효한_평가_작성자_아이디);
    protected CreateReviewRequest 사용자_평가_등록_요청 =
            new CreateReviewRequest(유효한_경매_아이디, 유효한_평가_대상_아이디, "친절하다.", 5.0f);
    protected CreateReviewRequest 중복된_평가_등록_요청 =
            new CreateReviewRequest(사용자가_이미_평가한_경매_아이디, 유효한_평가_대상_아이디, "친절하다.", 5.0f);
    protected Long 생성된_평가_아이디 = 1L;
    protected Long 구매자가_판매자1에게_받은_평가_아이디 = 2L;
    protected Long 구매자가_판매자2에게_받은_평가_아이디 = 3L;
    protected ReadMultipleReviewDto 구매자가_판매자1에게_받은_평가 =
            new ReadMultipleReviewDto(구매자가_판매자1에게_받은_평가_아이디, 판매자1, "친절하다.", 5.0d, LocalDateTime.now());
    protected ReadMultipleReviewDto 구매자가_판매자2에게_받은_평가 =
            new ReadMultipleReviewDto(구매자가_판매자2에게_받은_평가_아이디, 판매자2, "친절하다.", 5.0d, LocalDateTime.now());
    protected ReadSingleReviewDto 구매자가_판매자1에게_받은_평가_내용 = new ReadSingleReviewDto(
            구매자가_판매자1에게_받은_평가.score(),
            구매자가_판매자1에게_받은_평가.content()
    );
}
