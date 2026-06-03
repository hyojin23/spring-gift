package gift.auth;

import gift.member.Member;
import gift.member.MemberRequest;
import gift.member.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MemberAuthServiceTest {

    private final MemberService memberService = mock(MemberService.class);
    private final JwtProvider jwtProvider = mock(JwtProvider.class);
    private final MemberAuthService memberAuthService = new MemberAuthService(memberService, jwtProvider);

    @Test
    @DisplayName("회원가입에 성공하면 토큰을 반환한다")
    void register() {
        MemberRequest request = new MemberRequest("member@example.com", "password");
        Member member = new Member(request.email(), request.password());
        when(memberService.register(request)).thenReturn(member);
        when(jwtProvider.createToken(request.email())).thenReturn("token");

        TokenResponse response = memberAuthService.register(request);

        assertThat(response.token()).isEqualTo("token");
    }

    @Test
    @DisplayName("로그인에 성공하면 토큰을 반환한다")
    void login() {
        MemberRequest request = new MemberRequest("member@example.com", "password");
        Member member = new Member(request.email(), request.password());
        when(memberService.authenticate(request)).thenReturn(member);
        when(jwtProvider.createToken(request.email())).thenReturn("token");

        TokenResponse response = memberAuthService.login(request);

        assertThat(response.token()).isEqualTo("token");
    }
}
