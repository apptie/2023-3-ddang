package com.ddang.ddang.user.infrastructure.persistence.fixture;

import com.ddang.ddang.image.domain.ProfileImage;
import com.ddang.ddang.user.domain.Reliability;
import com.ddang.ddang.user.domain.User;
import com.ddang.ddang.user.domain.UserReliability;
import com.ddang.ddang.user.domain.repository.UserReliabilityRepository;
import com.ddang.ddang.user.domain.repository.UserRepository;
import com.ddang.ddang.user.infrastructure.persistence.JpaUserReliabilityRepository;
import com.ddang.ddang.user.infrastructure.persistence.JpaUserRepository;
import com.ddang.ddang.user.infrastructure.persistence.UserReliabilityRepositoryImpl;
import com.ddang.ddang.user.infrastructure.persistence.UserRepositoryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;

@SuppressWarnings("NonAsciiCharacters")
public class UserReliabilityRepositoryImplFixture {

    private UserRepository userRepository;
    private UserReliabilityRepository userReliabilityRepository;

    protected User 사용자_1;
    protected User 사용자_2;
    protected UserReliability 저장하기_전_사용자_신뢰도_엔티티;
    protected UserReliability 사용자_신뢰도_정보;

    @BeforeEach
    void setUp(
            @Autowired final JpaUserRepository jpaUserRepository,
            @Autowired final JpaUserReliabilityRepository jpaUserReliabilityRepository
    ) {
        userRepository = new UserRepositoryImpl(jpaUserRepository);
        userReliabilityRepository = new UserReliabilityRepositoryImpl(jpaUserReliabilityRepository);

        사용자_1 = User.builder()
                    .name("사용자1")
                    .profileImage(new ProfileImage("uplad.png", "store.png"))
                    .reliability(Reliability.INITIAL_RELIABILITY)
                    .oauthId("12345")
                    .build();
        사용자_2 = User.builder()
                    .name("사용자2")
                    .profileImage(new ProfileImage("uplad.png", "store.png"))
                    .reliability(Reliability.INITIAL_RELIABILITY)
                    .oauthId("54321")
                    .build();
        userRepository.save(사용자_1);
        userRepository.save(사용자_2);

        저장하기_전_사용자_신뢰도_엔티티 = new UserReliability(사용자_1);

        사용자_신뢰도_정보 = new UserReliability(사용자_2);
        userReliabilityRepository.save(사용자_신뢰도_정보);
    }
}
