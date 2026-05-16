package gift.global;

import gift.category.CategoryNotFoundException;
import gift.global.exception.ErrorResponse;
import gift.wish.exception.AuthenticationException;
import gift.wish.exception.UnauthorizedWishAccessException;
import gift.wish.exception.WishNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CategoryNotFoundException.class)
    public ResponseEntity<Void> handleCategoryNotFound() {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthentication(AuthenticationException exception) {
        return error(HttpStatus.UNAUTHORIZED, "AUTH.UNAUTHORIZED", exception.getMessage());
    }

    @ExceptionHandler(UnauthorizedWishAccessException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorizedWishAccess(UnauthorizedWishAccessException exception) {
        return error(HttpStatus.FORBIDDEN, "WISH.ACCESS_DENIED", exception.getMessage());
    }

    @ExceptionHandler(WishNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleWishNotFound(WishNotFoundException exception) {
        return error(HttpStatus.NOT_FOUND, "WISH.NOT_FOUND", exception.getMessage());
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
