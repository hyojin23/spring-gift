package gift.auth;

import gift.auth.exception.JwtTokenException;
import gift.member.Member;
import gift.member.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AuthenticationResolverTest {

    private final JwtProvider jwtProvider = mock(JwtProvider.class);
    private final MemberRepository memberRepository = mock(MemberRepository.class);
    private final AuthenticationResolver authenticationResolver = new AuthenticationResolver(
        jwtProvider,
        memberRepository
    );

    @Test
    @DisplayName("Bearer 토큰이 유효하고 회원이 있으면 회원을 반환한다")
    void extractMember() {
        Member member = new Member("member@example.com", "password");
        when(jwtProvider.getEmail("valid-token")).thenReturn("member@example.com");
        when(memberRepository.findByEmail("member@example.com")).thenReturn(Optional.of(member));

        Member result = authenticationResolver.extractMember("Bearer valid-token");

        assertThat(result).isEqualTo(member);
        verify(jwtProvider).getEmail("valid-token");
        verify(memberRepository).findByEmail("member@example.com");
    }

    @Test
    @DisplayName("Authorization 헤더가 null이면 null을 반환하고 JWT를 파싱하지 않는다")
    void extractMemberWithNullAuthorization() {
        Member result = authenticationResolver.extractMember(null);

        assertThat(result).isNull();
        verify(jwtProvider, never()).getEmail(org.mockito.ArgumentMatchers.any());
        verify(memberRepository, never()).findByEmail(org.mockito.ArgumentMatchers.any());
    }

    @Test
    @DisplayName("Authorization 헤더가 blank이면 null을 반환하고 JWT를 파싱하지 않는다")
    void extractMemberWithBlankAuthorization() {
        Member result = authenticationResolver.extractMember("   ");

        assertThat(result).isNull();
        verify(jwtProvider, never()).getEmail(org.mockito.ArgumentMatchers.any());
        verify(memberRepository, never()).findByEmail(org.mockito.ArgumentMatchers.any());
    }

    @Test
    @DisplayName("Bearer 형식이 아니면 null을 반환하고 JWT를 파싱하지 않는다")
    void extractMemberWithNonBearerAuthorization() {
        Member result = authenticationResolver.extractMember("Basic token");

        assertThat(result).isNull();
        verify(jwtProvider, never()).getEmail(org.mockito.ArgumentMatchers.any());
        verify(memberRepository, never()).findByEmail(org.mockito.ArgumentMatchers.any());
    }

    @Test
    @DisplayName("JWT 파싱에 실패하면 null을 반환하고 회원을 조회하지 않는다")
    void extractMemberWithInvalidToken() {
        when(jwtProvider.getEmail("invalid-token")).thenThrow(new JwtTokenException("유효하지 않은 JWT 토큰입니다."));

        Member result = authenticationResolver.extractMember("Bearer invalid-token");

        assertThat(result).isNull();
        verify(memberRepository, never()).findByEmail(org.mockito.ArgumentMatchers.any());
    }

    @Test
    @DisplayName("토큰 이메일에 해당하는 회원이 없으면 null을 반환한다")
    void extractMemberNotFound() {
        when(jwtProvider.getEmail("valid-token")).thenReturn("member@example.com");
        when(memberRepository.findByEmail("member@example.com")).thenReturn(Optional.empty());

        Member result = authenticationResolver.extractMember("Bearer valid-token");

        assertThat(result).isNull();
    }
}
