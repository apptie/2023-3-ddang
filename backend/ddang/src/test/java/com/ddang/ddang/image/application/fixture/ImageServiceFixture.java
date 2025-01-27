package com.ddang.ddang.image.application.fixture;

import com.ddang.ddang.image.domain.AuctionImage;
import com.ddang.ddang.image.domain.ProfileImage;
import com.ddang.ddang.image.infrastructure.persistence.JpaAuctionImageRepository;
import com.ddang.ddang.image.infrastructure.persistence.JpaProfileImageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;

@SuppressWarnings("NonAsciiCharacters")
public class ImageServiceFixture {

    @Autowired
    private JpaProfileImageRepository profileImageRepository;

    @Autowired
    private JpaAuctionImageRepository auctionImageRepository;

    protected String 존재하지_않는_프로필_이미지_이름 = "invalid-image.png";
    protected String 존재하지_않는_경매_이미지_이름 = "invalid-image.png";

    protected ProfileImage 프로필_이미지;
    protected String 프로필_이미지_파일명;

    protected AuctionImage 경매_이미지;
    protected String 경매_이미지_파일명;

    @BeforeEach
    void setUp() {
        프로필_이미지 = new ProfileImage("upload.png", "store.png");
        프로필_이미지_파일명 = 프로필_이미지.getStoreName();

        경매_이미지 = new AuctionImage("upload.png", "store.png");
        경매_이미지_파일명 = 경매_이미지.getStoreName();

        profileImageRepository.save(프로필_이미지);
        auctionImageRepository.save(경매_이미지);
    }
}
