package gift.member;

import gift.member.exception.MemberValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MemberTest {

    @Test
    @DisplayName("일반 회원 이메일이 비어 있으면 생성할 수 없다")
    void createBlankEmail() {
        assertThatThrownBy(() -> new Member(" ", "password"))
            .isInstanceOf(MemberValidationException.class)
            .hasMessage("회원 이메일은 필수입니다.");
    }

    @Test
    @DisplayName("일반 회원 비밀번호가 비어 있으면 생성할 수 없다")
    void createBlankPassword() {
        assertThatThrownBy(() -> new Member("member@example.com", " "))
            .isInstanceOf(MemberValidationException.class)
            .hasMessage("회원 비밀번호는 필수입니다.");
    }

    @Test
    @DisplayName("카카오 회원 이메일이 비어 있으면 생성할 수 없다")
    void createKakaoMemberBlankEmail() {
        assertThatThrownBy(() -> new Member(" "))
            .isInstanceOf(MemberValidationException.class)
            .hasMessage("회원 이메일은 필수입니다.");
    }

    @Test
    @DisplayName("카카오 회원은 비밀번호 없이 생성할 수 있다")
    void createKakaoMemberWithoutPassword() {
        Member member = new Member("kakao@example.com");

        assertThat(member.getEmail()).isEqualTo("kakao@example.com");
        assertThat(member.getPassword()).isNull();
    }

    @Test
    @DisplayName("회원 수정 시 이메일이 비어 있으면 수정할 수 없다")
    void updateBlankEmail() {
        Member member = member();

        assertThatThrownBy(() -> member.update(" ", "new-password"))
            .isInstanceOf(MemberValidationException.class)
            .hasMessage("회원 이메일은 필수입니다.");
    }

    @Test
    @DisplayName("회원 수정 시 비밀번호가 비어 있으면 수정할 수 없다")
    void updateBlankPassword() {
        Member member = member();

        assertThatThrownBy(() -> member.update("new-member@example.com", " "))
            .isInstanceOf(MemberValidationException.class)
            .hasMessage("회원 비밀번호는 필수입니다.");
    }

    @Test
    @DisplayName("회원 수정 검증에 실패하면 기존 상태를 유지한다")
    void updateInvalidValueKeepOriginalState() {
        Member member = member();

        assertThatThrownBy(() -> member.update("updated@example.com", " "))
            .isInstanceOf(MemberValidationException.class);

        assertThat(member.getEmail()).isEqualTo("member@example.com");
        assertThat(member.getPassword()).isEqualTo("password");
    }

    private Member member() {
        return new Member("member@example.com", "password");
    }
}
