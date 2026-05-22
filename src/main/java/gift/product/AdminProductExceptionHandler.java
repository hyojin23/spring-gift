package gift.product;

import gift.product.exception.AdminProductException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice(assignableTypes = AdminProductController.class)
public class AdminProductExceptionHandler {

    @ExceptionHandler(AdminProductException.class)
    public String handleAdminProductException(
        AdminProductException exception,
        RedirectAttributes redirectAttributes
    ) {
        redirectAttributes.addFlashAttribute("error", exception.getMessage());
        return "redirect:/admin/products";
    }
}
