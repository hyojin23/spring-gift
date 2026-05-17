package gift.member;

import gift.auth.JwtProvider;
import gift.member.exception.DuplicateMemberEmailException;
import gift.member.exception.InvalidMemberCredentialsException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MemberServiceTest {

    private final MemberRepository memberRepository = mock(MemberRepository.class);
    private final JwtProvider jwtProvider = mock(JwtProvider.class);
    private final MemberService memberService = new MemberService(memberRepository, jwtProvider);

    @Test
    @DisplayName("회원가입에 성공하면 토큰을 반환한다")
    void register() {
        MemberRequest request = new MemberRequest("member@example.com", "password");
        Member member = new Member(request.email(), request.password());
        when(memberRepository.existsByEmail(request.email())).thenReturn(false);
        when(memberRepository.save(org.mockito.ArgumentMatchers.any(Member.class))).thenReturn(member);
        when(jwtProvider.createToken(request.email())).thenReturn("token");

        assertThat(memberService.register(request).token()).isEqualTo("token");
    }

    @Test
    @DisplayName("중복 이메일로 회원가입하면 중복 이메일 예외를 던진다")
    void registerDuplicateEmail() {
        MemberRequest request = new MemberRequest("member@example.com", "password");
        when(memberRepository.existsByEmail(request.email())).thenReturn(true);

        assertThatThrownBy(() -> memberService.register(request))
            .isInstanceOf(DuplicateMemberEmailException.class);
    }

    @Test
    @DisplayName("로그인에 성공하면 토큰을 반환한다")
    void login() {
        MemberRequest request = new MemberRequest("member@example.com", "password");
        Member member = new Member(request.email(), request.password());
        when(memberRepository.findByEmail(request.email())).thenReturn(Optional.of(member));
        when(jwtProvider.createToken(request.email())).thenReturn("token");

        assertThat(memberService.login(request).token()).isEqualTo("token");
    }

    @Test
    @DisplayName("등록되지 않은 이메일로 로그인하면 인증 실패 예외를 던진다")
    void loginEmailNotFound() {
        MemberRequest request = new MemberRequest("member@example.com", "password");
        when(memberRepository.findByEmail(request.email())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> memberService.login(request))
            .isInstanceOf(InvalidMemberCredentialsException.class);
    }

    @Test
    @DisplayName("잘못된 비밀번호로 로그인하면 인증 실패 예외를 던진다")
    void loginWrongPassword() {
        MemberRequest request = new MemberRequest("member@example.com", "wrong-password");
        Member member = new Member(request.email(), "password");
        when(memberRepository.findByEmail(request.email())).thenReturn(Optional.of(member));

        assertThatThrownBy(() -> memberService.login(request))
            .isInstanceOf(InvalidMemberCredentialsException.class);
    }
}
