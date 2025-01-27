package com.ddang.ddang.image.infrastructure.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import com.ddang.ddang.configuration.QuerydslConfiguration;
import com.ddang.ddang.image.domain.AuctionImage;
import com.ddang.ddang.image.infrastructure.persistence.fixture.JpaAuctionImageRepositoryFixture;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.Optional;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@Import(QuerydslConfiguration.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
class JpaAuctionImageRepositoryTest extends JpaAuctionImageRepositoryFixture {

    @PersistenceContext
    EntityManager em;

    @Autowired
    JpaAuctionImageRepository auctionImageRepository;

    @Test
    void 경매_이미지를_저장한다() {
        // given
        final AuctionImage auctionImage = new AuctionImage(업로드_이미지_파일명, 저장된_이미지_파일명);

        // when
        final AuctionImage actual = auctionImageRepository.save(auctionImage);

        // then
        em.flush();
        em.clear();

        assertThat(actual.getId()).isPositive();
    }

    @Test
    void 지정한_파일_이름에_해당하는_경매_이미지를_조회한다() {
        // when
        final Optional<AuctionImage> actual = auctionImageRepository.findByStoreName(경매_이미지.getStoreName());

        // then
        assertThat(actual).contains(경매_이미지);
    }

    @Test
    void 지정한_파일_이름에_해당하는_경매_이미지가_없는_경우_빈_Optional을_반환한다() {
        // when
        final Optional<AuctionImage> actual = auctionImageRepository.findByStoreName(
                존재하지_않는_경매_이미지_파일_이름
        );

        // then
        assertThat(actual).isEmpty();
    }
}
