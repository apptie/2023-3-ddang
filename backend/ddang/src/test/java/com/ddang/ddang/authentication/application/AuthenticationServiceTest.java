package com.ddang.ddang.authentication.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

import com.ddang.ddang.auction.domain.repository.AuctionRepository;
import com.ddang.ddang.authentication.application.dto.response.LoginInfoDto;
import com.ddang.ddang.authentication.application.dto.response.TokenDto;
import com.ddang.ddang.authentication.application.exception.InvalidWithdrawalException;
import com.ddang.ddang.authentication.application.exception.WithdrawalNotAllowedException;
import com.ddang.ddang.authentication.application.fixture.AuthenticationServiceFixture;
import com.ddang.ddang.authentication.domain.Oauth2UserInformationProviderComposite;
import com.ddang.ddang.authentication.domain.RandomUuidUserNameGenerator;
import com.ddang.ddang.authentication.domain.TokenDecoder;
import com.ddang.ddang.authentication.domain.TokenEncoder;
import com.ddang.ddang.authentication.domain.exception.InvalidTokenException;
import com.ddang.ddang.authentication.infrastructure.oauth2.OAuth2UserInformationProvider;
import com.ddang.ddang.configuration.IsolateDatabase;
import com.ddang.ddang.device.application.DeviceTokenService;
import com.ddang.ddang.device.domain.repository.DeviceTokenRepository;
import com.ddang.ddang.user.domain.repository.UserRepository;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

@IsolateDatabase
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
class AuthenticationServiceTest extends AuthenticationServiceFixture {

    @MockBean
    Oauth2UserInformationProviderComposite providerComposite;

    @MockBean
    OAuth2UserInformationProvider userInfoProvider;

    @MockBean
    DeviceTokenService deviceTokenService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    AuctionRepository auctionRepository;

    @Autowired
    TokenEncoder tokenEncoder;

    @Autowired
    TokenDecoder tokenDecoder;

    @Autowired
    BlackListTokenService blackListTokenService;

    @Autowired
    DeviceTokenRepository deviceTokenRepository;

    AuthenticationService authenticationService;

    @BeforeEach
    void fixtureSetUp() {
        authenticationService = new AuthenticationService(
                deviceTokenService,
                providerComposite,
                userRepository,
                auctionRepository,
                tokenEncoder,
                tokenDecoder,
                blackListTokenService,
                deviceTokenRepository,
                new RandomUuidUserNameGenerator()
        );
    }

    @Test
    void 로그인할_때_가입하지_않은_사용자라면_회원가입을_진행한다() {
        // given
        given(providerComposite.findProvider(지원하는_소셜_로그인_타입)).willReturn(userInfoProvider);
        given(userInfoProvider.findUserInformation(anyString())).willReturn(가입하지_않은_사용자_회원_정보);

        // when
        final LoginInfoDto actual = authenticationService.login(
                가입하지_않은_사용자_회원_정보.findUserId(),
                지원하는_소셜_로그인_타입,
                로그인_요청_디바이스_토큰_정보
        );

        // then
        SoftAssertions.assertSoftly(softAssertions -> {
            softAssertions.assertThat(actual.accessToken()).isNotEmpty().contains("Bearer ");
            softAssertions.assertThat(actual.refreshToken()).isNotEmpty().contains("Bearer ");
        });
    }

    @Test
    void 가입한_회원이_소셜_로그인을_할_경우_accessToken과_refreshToken을_반환한다() {
        // given
        given(providerComposite.findProvider(지원하는_소셜_로그인_타입)).willReturn(userInfoProvider);
        given(userInfoProvider.findUserInformation(anyString())).willReturn(가입한_사용자_회원_정보);

        // when
        final LoginInfoDto actual = authenticationService.login(
                가입한_사용자_회원_정보.findUserId(),
                지원하는_소셜_로그인_타입,
                로그인_요청_디바이스_토큰_정보
        );

        // then
        SoftAssertions.assertSoftly(softAssertions -> {
            softAssertions.assertThat(actual.accessToken()).isNotEmpty().contains("Bearer ");
            softAssertions.assertThat(actual.refreshToken()).isNotEmpty().contains("Bearer ");
            softAssertions.assertThat(actual.isSignUpUser()).isFalse();
        });
    }

    @Test
    void 가입하지_않은_사용자가_소셜_로그인을_할_경우_accessToken과_refreshToken을_반환한다() {
        // given
        given(providerComposite.findProvider(지원하는_소셜_로그인_타입)).willReturn(userInfoProvider);
        given(userInfoProvider.findUserInformation(anyString())).willReturn(가입하지_않은_사용자_회원_정보);

        // when
        final LoginInfoDto actual = authenticationService.login(
                가입하지_않은_사용자_회원_정보.findUserId(),
                지원하는_소셜_로그인_타입,
                로그인_요청_디바이스_토큰_정보
        );

        // then
        SoftAssertions.assertSoftly(softAssertions -> {
            softAssertions.assertThat(actual.accessToken()).isNotEmpty();
            softAssertions.assertThat(actual.refreshToken()).isNotEmpty();
            softAssertions.assertThat(actual.isSignUpUser()).isTrue();
        });
    }

    @Test
    void refreshToken을_전달하면_새로운_accessToken을_반환한다() {
        // when
        final TokenDto actual = authenticationService.refreshToken(유효한_리프레시_토큰);

        // then
        SoftAssertions.assertSoftly(softAssertions -> {
            softAssertions.assertThat(actual.accessToken()).isNotEmpty();
            softAssertions.assertThat(actual.refreshToken()).isNotEmpty();
        });
    }

    @Test
    void 만료된_refreshToken으로_새로운_accessToken을_요청하면_예외가_발생한다() {
        // when & then
        assertThatThrownBy(() -> authenticationService.refreshToken(만료된_리프레시_토큰))
                .isInstanceOf(InvalidTokenException.class)
                .hasMessage("유효한 토큰이 아닙니다.");
    }

    @Test
    void 유효한_토큰_타입이_아닌_refreshToken으로_새로운_accessToken을_요청하면_예외가_발생한다() {
        // when & then
        assertThatThrownBy(() -> authenticationService.refreshToken(유효하지_않은_타입의_리프레시_토큰))
                .isInstanceOf(InvalidTokenException.class)
                .hasMessage("유효한 토큰이 아닙니다.");
    }

    @Test
    void 유효한_accessToken을_검증하면_참을_반환한다() {
        // when
        final boolean actual = authenticationService.validateToken(유효한_액세스_토큰);

        // then
        assertThat(actual).isTrue();
    }

    @Test
    void 만료된_accessToken을_검증하면_거짓을_반환한다() {
        // when
        final boolean actual = authenticationService.validateToken(만료된_소셜_로그인_토큰);

        // then
        assertThat(actual).isFalse();
    }

    @Test
    void 가입한_회원이_탈퇴하는_경우_정상처리한다() throws InvalidWithdrawalException {
        // given
        given(providerComposite.findProvider(지원하는_소셜_로그인_타입)).willReturn(userInfoProvider);
        given(userInfoProvider.findUserInformation(anyString())).willReturn(가입한_사용자_회원_정보);

        // when
        authenticationService.withdrawal(유효한_액세스_토큰, 유효한_리프레시_토큰);

        // then
        SoftAssertions.assertSoftly(softAssertions -> {
            softAssertions.assertThat(사용자.isDeleted()).isTrue();
            softAssertions.assertThat(사용자.findName()).isNotEqualTo(사용자_이름);
            softAssertions.assertThat(사용자.getProfileImage()).isNull();
        });
    }

    @Test
    void 이미_탈퇴한_회원이_탈퇴하는_경우_예외가_발생한다() {
        given(providerComposite.findProvider(지원하는_소셜_로그인_타입)).willReturn(userInfoProvider);
        given(userInfoProvider.findUserInformation(anyString())).willReturn(탈퇴한_사용자_회원_정보);
        given(userInfoProvider.unlinkUserBy(anyString())).willReturn(탈퇴한_사용자_회원_정보);

        // when && then
        assertThatThrownBy(() -> authenticationService.withdrawal(탈퇴한_사용자_액세스_토큰, 유효한_리프레시_토큰))
                .isInstanceOf(InvalidWithdrawalException.class)
                .hasMessage("탈퇴에 대한 권한이 없습니다.");
    }

    @Test
    void 존재하지_않는_회원이_탈퇴하는_경우_예외가_발생한다() {
        // given
        given(providerComposite.findProvider(지원하는_소셜_로그인_타입)).willReturn(userInfoProvider);
        given(userInfoProvider.findUserInformation(anyString())).willThrow(
                new InvalidTokenException("401 Unauthorized"));

        // when & then
        assertThatThrownBy(() -> authenticationService.withdrawal(존재하지_않는_사용자_액세스_토큰, 유효한_리프레시_토큰))
                .isInstanceOf(InvalidWithdrawalException.class)
                .hasMessage("탈퇴에 대한 권한이 없습니다.");
    }

    @Test
    void 탈퇴할_때_유효한_토큰이_아닌_경우_예외가_발생한다() {
        // when & then
        assertThatThrownBy(() -> authenticationService.withdrawal(유효하지_않은_액세스_토큰, 유효한_리프레시_토큰))
                .isInstanceOf(InvalidTokenException.class)
                .hasMessage("유효한 토큰이 아닙니다.");
    }

    @Test
    void 탈퇴할_때_등록한_경매중_진행중인_경매가_있다면_예외가_발생한다() {
        // given
        given(providerComposite.findProvider(지원하는_소셜_로그인_타입)).willReturn(userInfoProvider);
        given(userInfoProvider.findUserInformation(anyString())).willReturn(현재_진행중인_경매가_있는_사용자_회원_정보);

        // when & then
        assertThatThrownBy(
                () -> authenticationService.withdrawal(현재_진행중인_경매가_있는_사용자_액세스_토큰, 현재_진행중인_경매가_있는_사용자_리프래시_토큰))
                .isInstanceOf(WithdrawalNotAllowedException.class)
                .hasMessage("진행 중인 경매가 있기 때문에 탈퇴할 수 없습니다.");
    }

    @Test
    void 탈퇴할_때_등록한_경매중_진행중인_경매의_마지막_입찰자라면_예외가_발생한다() {
        // given
        given(providerComposite.findProvider(지원하는_소셜_로그인_타입)).willReturn(userInfoProvider);
        given(userInfoProvider.findUserInformation(anyString())).willReturn(현재_진행중인_경매가_있는_사용자_회원_정보);

        // when & then
        assertThatThrownBy(() -> authenticationService.withdrawal(현재_진행중인_경매의_마지막_입찰자인_사용자_액세스_토큰,
                현재_진행중인_경매의_마지막_입찰자인_사용자_리프래시_토큰))
                .isInstanceOf(WithdrawalNotAllowedException.class)
                .hasMessage("진행 중인 경매가 있기 때문에 탈퇴할 수 없습니다.");
    }
}
