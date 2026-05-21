package gift.auth;

import gift.auth.exception.KakaoLoginException;
import gift.member.Member;
import gift.member.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class KakaoAuthServiceTest {

    private final KakaoLoginClient kakaoLoginClient = mock(KakaoLoginClient.class);
    private final MemberRepository memberRepository = mock(MemberRepository.class);
    private final JwtProvider jwtProvider = mock(JwtProvider.class);
    private final KakaoAuthService kakaoAuthService = new KakaoAuthService(
        kakaoLoginClient,
        memberRepository,
        jwtProvider
    );

    @Test
    @DisplayName("신규 카카오 사용자이면 회원을 생성하고 JWT 토큰을 반환한다")
    void loginWithNewKakaoMember() {
        when(kakaoLoginClient.requestAccessToken("authorization-code"))
            .thenReturn(new KakaoLoginClient.KakaoTokenResponse("kakao-access-token"));
        when(kakaoLoginClient.requestUserInfo("kakao-access-token"))
            .thenReturn(kakaoUserResponse("member@example.com"));
        when(memberRepository.findByEmail("member@example.com")).thenReturn(Optional.empty());
        when(jwtProvider.createToken("member@example.com")).thenReturn("service-jwt-token");

        String token = kakaoAuthService.login("authorization-code");

        assertThat(token).isEqualTo("service-jwt-token");
        ArgumentCaptor<Member> memberCaptor = ArgumentCaptor.forClass(Member.class);
        verify(memberRepository).save(memberCaptor.capture());
        Member savedMember = memberCaptor.getValue();
        assertThat(savedMember.getEmail()).isEqualTo("member@example.com");
        assertThat(savedMember.getKakaoAccessToken()).isEqualTo("kakao-access-token");
    }

    @Test
    @DisplayName("기존 카카오 사용자이면 access token을 갱신하고 JWT 토큰을 반환한다")
    void loginWithExistingKakaoMember() {
        Member member = new Member("member@example.com");
        member.updateKakaoAccessToken("old-token");
        when(kakaoLoginClient.requestAccessToken("authorization-code"))
            .thenReturn(new KakaoLoginClient.KakaoTokenResponse("new-kakao-access-token"));
        when(kakaoLoginClient.requestUserInfo("new-kakao-access-token"))
            .thenReturn(kakaoUserResponse("member@example.com"));
        when(memberRepository.findByEmail("member@example.com")).thenReturn(Optional.of(member));
        when(jwtProvider.createToken("member@example.com")).thenReturn("service-jwt-token");

        String token = kakaoAuthService.login("authorization-code");

        assertThat(token).isEqualTo("service-jwt-token");
        assertThat(member.getKakaoAccessToken()).isEqualTo("new-kakao-access-token");
        verify(memberRepository).save(member);
    }

    @Test
    @DisplayName("카카오 access token이 비어 있으면 카카오 로그인 예외가 발생한다")
    void loginWithBlankAccessToken() {
        when(kakaoLoginClient.requestAccessToken("authorization-code"))
            .thenReturn(new KakaoLoginClient.KakaoTokenResponse(" "));

        assertThatThrownBy(() -> kakaoAuthService.login("authorization-code"))
            .isInstanceOf(KakaoLoginException.class)
            .hasMessage("카카오 access token이 비어 있습니다.");
        verify(memberRepository, never()).save(org.mockito.ArgumentMatchers.any());
    }

    @Test
    @DisplayName("카카오 사용자 이메일이 비어 있으면 카카오 로그인 예외가 발생한다")
    void loginWithBlankEmail() {
        when(kakaoLoginClient.requestAccessToken("authorization-code"))
            .thenReturn(new KakaoLoginClient.KakaoTokenResponse("kakao-access-token"));
        when(kakaoLoginClient.requestUserInfo("kakao-access-token"))
            .thenReturn(kakaoUserResponse(" "));

        assertThatThrownBy(() -> kakaoAuthService.login("authorization-code"))
            .isInstanceOf(KakaoLoginException.class)
            .hasMessage("카카오 사용자 이메일이 비어 있습니다.");
        verify(memberRepository, never()).save(org.mockito.ArgumentMatchers.any());
    }

    private KakaoLoginClient.KakaoUserResponse kakaoUserResponse(String email) {
        return new KakaoLoginClient.KakaoUserResponse(
            new KakaoLoginClient.KakaoUserResponse.KakaoAccount(email)
        );
    }
}
