package com.ddang.ddang.image.application;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ddang.ddang.configuration.IsolateDatabase;
import com.ddang.ddang.image.application.fixture.ImageServiceFixture;
import com.ddang.ddang.image.infrastructure.persistence.exception.AuctionImageNotFoundException;
import java.net.MalformedURLException;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;

@IsolateDatabase
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
class ImageServiceTest extends ImageServiceFixture {

    @Autowired
    ImageService imageService;

    @Test
    void 지정한_아이디에_해당하는_프로필_이미지를_조회한다() throws Exception {
        // when
        final Resource actual = imageService.readProfileImage(프로필_이미지.getStoreName());

        // then
        assertThat(actual.getFilename()).isEqualTo(프로필_이미지_파일명);
    }

    @Test
    void 지정한_아이디에_해당하는_프로필_이미지가_없는_경우_null을_반환한다() throws MalformedURLException {
        // when
        final Resource actual = imageService.readProfileImage(존재하지_않는_프로필_이미지_이름);

        // then
        assertThat(actual).isNull();
    }

    @Test
    void 지정한_아이디에_해당하는_경매_이미지를_조회한다() throws Exception {
        // when
        final Resource actual = imageService.readAuctionImage(경매_이미지.getImage().getStoreName());

        // then
        assertThat(actual.getFilename()).isEqualTo(경매_이미지_파일명);
    }

    @Test
    void 지정한_아이디에_해당하는_경매_이미지가_없는_경우_예외가_발생한다() {
        // when & then
        assertThatThrownBy(() -> imageService.readAuctionImage(존재하지_않는_경매_이미지_이름))
                .isInstanceOf(AuctionImageNotFoundException.class);
    }
}
