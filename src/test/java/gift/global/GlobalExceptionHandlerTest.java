package gift.global;

import gift.global.exception.ErrorResponse;
import gift.wish.exception.AuthenticationException;
import gift.wish.exception.UnauthorizedWishAccessException;
import gift.wish.exception.WishNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    @DisplayName("인증 예외를 401 에러 응답으로 변환한다")
    void handleAuthentication() {
        ResponseEntity<ErrorResponse> response = handler.handleAuthentication(new AuthenticationException());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().code()).isEqualTo("AUTH.UNAUTHORIZED");
    }

    @Test
    @DisplayName("위시 접근 권한 예외를 403 에러 응답으로 변환한다")
    void handleUnauthorizedWishAccess() {
        ResponseEntity<ErrorResponse> response = handler.handleUnauthorizedWishAccess(
            new UnauthorizedWishAccessException()
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().code()).isEqualTo("WISH.ACCESS_DENIED");
    }

    @Test
    @DisplayName("위시 미존재 예외를 404 에러 응답으로 변환한다")
    void handleWishNotFound() {
        ResponseEntity<ErrorResponse> response = handler.handleWishNotFound(new WishNotFoundException());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().code()).isEqualTo("WISH.NOT_FOUND");
    }
}
