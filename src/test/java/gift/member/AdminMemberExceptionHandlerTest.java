package gift.member;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import static org.assertj.core.api.Assertions.assertThat;

class AdminMemberExceptionHandlerTest {

    private final AdminMemberExceptionHandler handler = new AdminMemberExceptionHandler();

    @Test
    @DisplayName("예상하지 못한 예외를 관리자 회원 목록 redirect 응답으로 변환한다")
    void handleUnexpected() {
        RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();

        String viewName = handler.handleUnexpected(new RuntimeException("unexpected"), redirectAttributes);

        assertThat(viewName).isEqualTo("redirect:/admin/members");
        assertThat(redirectAttributes.getFlashAttributes().get("error"))
            .isEqualTo("요청을 처리할 수 없습니다.");
    }
}
