package gift.global;

import gift.category.exception.CategoryNotFoundException;
import gift.category.exception.CategoryValidationException;
import gift.global.exception.ErrorResponse;
import gift.member.exception.DuplicateMemberEmailException;
import gift.member.exception.InsufficientMemberPointException;
import gift.member.exception.InvalidMemberCredentialsException;
import gift.option.exception.DuplicateOptionNameException;
import gift.option.exception.OptionDeletionNotAllowedException;
import gift.option.exception.OptionNotFoundException;
import gift.option.exception.OptionProductNotFoundException;
import gift.option.exception.OptionQuantityException;
import gift.option.exception.OptionValidationException;
import gift.order.exception.OrderException;
import gift.product.exception.ProductCategoryNotFoundException;
import gift.product.exception.ProductNotFoundException;
import gift.product.exception.ProductValidationException;
import gift.auth.exception.AuthenticationException;
import gift.wish.exception.UnauthorizedWishAccessException;
import gift.wish.exception.WishNotFoundException;
import gift.wish.exception.WishProductNotFoundException;
import gift.wish.exception.WishValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CategoryNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleCategoryNotFound(CategoryNotFoundException exception) {
        return error(HttpStatus.NOT_FOUND, "CATEGORY.NOT_FOUND", exception.getMessage());
    }

    @ExceptionHandler(CategoryValidationException.class)
    public ResponseEntity<ErrorResponse> handleCategoryValidation(CategoryValidationException exception) {
        return error(HttpStatus.BAD_REQUEST, "CATEGORY.INVALID", exception.getMessage());
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthentication(AuthenticationException exception) {
        return error(HttpStatus.UNAUTHORIZED, "AUTH.UNAUTHORIZED", exception.getMessage());
    }

    @ExceptionHandler(DuplicateMemberEmailException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateMemberEmail(DuplicateMemberEmailException exception) {
        return error(HttpStatus.BAD_REQUEST, "MEMBER.DUPLICATE_EMAIL", exception.getMessage());
    }

    @ExceptionHandler(InvalidMemberCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleInvalidMemberCredentials(InvalidMemberCredentialsException exception) {
        return error(HttpStatus.UNAUTHORIZED, "MEMBER.INVALID_CREDENTIALS", exception.getMessage());
    }

    @ExceptionHandler(InsufficientMemberPointException.class)
    public ResponseEntity<ErrorResponse> handleInsufficientMemberPoint(InsufficientMemberPointException exception) {
        return error(HttpStatus.BAD_REQUEST, "MEMBER.INSUFFICIENT_POINT", exception.getMessage());
    }

    @ExceptionHandler(UnauthorizedWishAccessException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorizedWishAccess(UnauthorizedWishAccessException exception) {
        return error(HttpStatus.FORBIDDEN, "WISH.ACCESS_DENIED", exception.getMessage());
    }

    @ExceptionHandler(WishNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleWishNotFound(WishNotFoundException exception) {
        return error(HttpStatus.NOT_FOUND, "WISH.NOT_FOUND", exception.getMessage());
    }

    @ExceptionHandler(WishProductNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleWishProductNotFound(WishProductNotFoundException exception) {
        return error(HttpStatus.NOT_FOUND, "WISH.PRODUCT_NOT_FOUND", exception.getMessage());
    }

    @ExceptionHandler(WishValidationException.class)
    public ResponseEntity<ErrorResponse> handleWishValidation(WishValidationException exception) {
        return error(HttpStatus.BAD_REQUEST, "WISH.INVALID", exception.getMessage());
    }

    @ExceptionHandler(OptionProductNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleOptionProductNotFound(OptionProductNotFoundException exception) {
        return error(HttpStatus.NOT_FOUND, "OPTION.PRODUCT_NOT_FOUND", exception.getMessage());
    }

    @ExceptionHandler(OptionNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleOptionNotFound(OptionNotFoundException exception) {
        return error(HttpStatus.NOT_FOUND, "OPTION.NOT_FOUND", exception.getMessage());
    }

    @ExceptionHandler(DuplicateOptionNameException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateOptionName(DuplicateOptionNameException exception) {
        return error(HttpStatus.BAD_REQUEST, "OPTION.DUPLICATE_NAME", exception.getMessage());
    }

    @ExceptionHandler(OptionDeletionNotAllowedException.class)
    public ResponseEntity<ErrorResponse> handleOptionDeletionNotAllowed(OptionDeletionNotAllowedException exception) {
        return error(HttpStatus.BAD_REQUEST, "OPTION.DELETE_NOT_ALLOWED", exception.getMessage());
    }

    @ExceptionHandler(OptionValidationException.class)
    public ResponseEntity<ErrorResponse> handleOptionValidation(OptionValidationException exception) {
        return error(HttpStatus.BAD_REQUEST, "OPTION.INVALID_NAME", exception.getMessage());
    }

    @ExceptionHandler(OptionQuantityException.class)
    public ResponseEntity<ErrorResponse> handleOptionQuantity(OptionQuantityException exception) {
        return error(HttpStatus.BAD_REQUEST, "OPTION.INVALID_QUANTITY", exception.getMessage());
    }

    @ExceptionHandler(OrderException.class)
    public ResponseEntity<ErrorResponse> handleOrder(OrderException exception) {
        return error(
            exception.getErrorCode().getStatus(),
            exception.getErrorCode().getCode(),
            exception.getMessage()
        );
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleProductNotFound(ProductNotFoundException exception) {
        return error(HttpStatus.NOT_FOUND, "PRODUCT.NOT_FOUND", exception.getMessage());
    }

    @ExceptionHandler(ProductCategoryNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleProductCategoryNotFound(ProductCategoryNotFoundException exception) {
        return error(HttpStatus.NOT_FOUND, "PRODUCT.CATEGORY_NOT_FOUND", exception.getMessage());
    }

    @ExceptionHandler(ProductValidationException.class)
    public ResponseEntity<ErrorResponse> handleProductValidation(ProductValidationException exception) {
        return error(HttpStatus.BAD_REQUEST, "PRODUCT.INVALID_NAME", exception.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpected(Exception exception) {
        return error(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL.SERVER_ERROR", "예기치 않은 오류가 발생했습니다.");
    }

    private ResponseEntity<ErrorResponse> error(HttpStatus status, String code, String message) {
        return ResponseEntity.status(status)
            .body(ErrorResponse.of(code, message));
    }
}
