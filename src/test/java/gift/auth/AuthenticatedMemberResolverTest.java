package gift.auth;

import gift.member.Member;
import gift.wish.exception.AuthenticationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AuthenticatedMemberResolverTest {

    private final AuthenticationResolver authenticationResolver = mock(AuthenticationResolver.class);
    private final AuthenticatedMemberResolver authenticatedMemberResolver = new AuthenticatedMemberResolver(
        authenticationResolver
    );

    @Test
    @DisplayName("인증된 회원을 반환한다")
    void resolve() {
        Member member = new Member("member@example.com", "password");
        when(authenticationResolver.extractMember("Bearer valid-token")).thenReturn(member);

        Member result = authenticatedMemberResolver.resolve("Bearer valid-token");

        assertThat(result).isEqualTo(member);
    }

    @Test
    @DisplayName("Authorization 헤더가 유효하지 않으면 인증 예외가 발생한다")
    void resolveWithInvalidAuthorization() {
        when(authenticationResolver.extractMember("Bearer invalid-token")).thenReturn(null);

        assertThatThrownBy(() -> authenticatedMemberResolver.resolve("Bearer invalid-token"))
            .isInstanceOf(AuthenticationException.class)
            .hasMessage("인증 정보가 없거나 유효하지 않습니다.");
    }

    @Test
    @DisplayName("Authorization 헤더가 null이면 인증 예외가 발생한다")
    void resolveWithNullAuthorization() {
        when(authenticationResolver.extractMember(null)).thenReturn(null);

        assertThatThrownBy(() -> authenticatedMemberResolver.resolve(null))
            .isInstanceOf(AuthenticationException.class)
            .hasMessage("인증 정보가 없거나 유효하지 않습니다.");
    }

    @Test
    @DisplayName("Authorization 헤더가 blank이면 인증 예외가 발생한다")
    void resolveWithBlankAuthorization() {
        when(authenticationResolver.extractMember("   ")).thenReturn(null);

        assertThatThrownBy(() -> authenticatedMemberResolver.resolve("   "))
            .isInstanceOf(AuthenticationException.class)
            .hasMessage("인증 정보가 없거나 유효하지 않습니다.");
    }
}
