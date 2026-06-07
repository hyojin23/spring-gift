package gift.member;

import gift.member.exception.MemberException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice(assignableTypes = AdminMemberController.class)
public class AdminMemberExceptionHandler {

    @ExceptionHandler(MemberException.class)
    public String handleMemberException(MemberException exception, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("error", exception.getMessage());
        return "redirect:/admin/members";
    }

    @ExceptionHandler(Exception.class)
    public String handleUnexpected(Exception exception, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("error", "요청을 처리할 수 없습니다.");
        return "redirect:/admin/members";
    }
}
