package gift.member;

import gift.member.exception.DuplicateMemberEmailException;
import gift.member.exception.InvalidMemberCredentialsException;
import gift.member.exception.PointDeductionTargetNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MemberServiceTest {

    private final MemberRepository memberRepository = mock(MemberRepository.class);
    private final PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
    private final MemberService memberService = new MemberService(memberRepository, passwordEncoder);

    @Test
    @DisplayName("회원가입에 성공하면 회원을 저장하고 반환한다")
    void register() {
        MemberRequest request = new MemberRequest("member@example.com", "password");
        Member member = new Member(request.email(), "encoded-password");
        when(memberRepository.existsByEmail(request.email())).thenReturn(false);
        when(passwordEncoder.encode(request.password())).thenReturn("encoded-password");
        when(memberRepository.saveAndFlush(any(Member.class))).thenReturn(member);

        Member result = memberService.register(request);

        assertThat(result.getEmail()).isEqualTo(request.email());
        assertThat(result.getPassword()).isEqualTo("encoded-password");
        verify(passwordEncoder).encode(request.password());
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
    @DisplayName("회원 저장 시 DB unique 제약 위반이 발생하면 중복 이메일 예외를 던진다")
    void registerDuplicateEmailByDatabaseConstraint() {
        MemberRequest request = new MemberRequest("member@example.com", "password");
        when(memberRepository.existsByEmail(request.email())).thenReturn(false);
        when(passwordEncoder.encode(request.password())).thenReturn("encoded-password");
        when(memberRepository.saveAndFlush(any(Member.class)))
            .thenThrow(new DataIntegrityViolationException("duplicate email"));

        assertThatThrownBy(() -> memberService.register(request))
            .isInstanceOf(DuplicateMemberEmailException.class);
    }

    @Test
    @DisplayName("인증에 성공하면 회원을 반환한다")
    void authenticate() {
        MemberRequest request = new MemberRequest("member@example.com", "password");
        Member member = new Member(request.email(), "encoded-password");
        when(memberRepository.findByEmail(request.email())).thenReturn(Optional.of(member));
        when(passwordEncoder.matches(request.password(), member.getPassword())).thenReturn(true);

        Member result = memberService.authenticate(request);

        assertThat(result).isEqualTo(member);
        verify(passwordEncoder).matches(request.password(), member.getPassword());
    }

    @Test
    @DisplayName("등록되지 않은 이메일로 로그인하면 인증 실패 예외를 던진다")
    void loginEmailNotFound() {
        MemberRequest request = new MemberRequest("member@example.com", "password");
        when(memberRepository.findByEmail(request.email())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> memberService.authenticate(request))
            .isInstanceOf(InvalidMemberCredentialsException.class);
    }

    @Test
    @DisplayName("잘못된 비밀번호로 로그인하면 인증 실패 예외를 던진다")
    void loginWrongPassword() {
        MemberRequest request = new MemberRequest("member@example.com", "wrong-password");
        Member member = new Member(request.email(), "encoded-password");
        when(memberRepository.findByEmail(request.email())).thenReturn(Optional.of(member));
        when(passwordEncoder.matches(request.password(), member.getPassword())).thenReturn(false);

        assertThatThrownBy(() -> memberService.authenticate(request))
            .isInstanceOf(InvalidMemberCredentialsException.class);
    }

    @Test
    @DisplayName("주문 시 회원 포인트를 차감한다")
    void deductPointForOrder() {
        Member member = new Member("member@example.com", "password");
        member.chargePoint(10_000);
        when(memberRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(member));

        Member result = memberService.deductPointForOrder(1L, 2_000);

        assertThat(result.getPoint()).isEqualTo(8_000);
        verify(memberRepository).findByIdForUpdate(1L);
    }

    @Test
    @DisplayName("포인트 차감 대상을 찾지 못하면 포인트 차감 대상 미존재 예외를 던진다")
    void deductPointForOrderTargetNotFound() {
        when(memberRepository.findByIdForUpdate(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> memberService.deductPointForOrder(1L, 2_000))
            .isInstanceOf(PointDeductionTargetNotFoundException.class);
    }
}
