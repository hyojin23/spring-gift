package gift.global;

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
import gift.order.exception.OrderOptionNotFoundException;
import gift.order.exception.OrderValidationException;
import gift.product.exception.ProductCategoryNotFoundException;
import gift.product.exception.ProductNotFoundException;
import gift.product.exception.ProductValidationException;
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
    @DisplayName("회원 중복 이메일 예외를 400 에러 응답으로 변환한다")
    void handleDuplicateMemberEmail() {
        ResponseEntity<ErrorResponse> response = handler.handleDuplicateMemberEmail(
            new DuplicateMemberEmailException()
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().code()).isEqualTo("MEMBER.DUPLICATE_EMAIL");
    }

    @Test
    @DisplayName("회원 로그인 실패 예외를 401 에러 응답으로 변환한다")
    void handleInvalidMemberCredentials() {
        ResponseEntity<ErrorResponse> response = handler.handleInvalidMemberCredentials(
            new InvalidMemberCredentialsException()
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().code()).isEqualTo("MEMBER.INVALID_CREDENTIALS");
    }

    @Test
    @DisplayName("회원 포인트 부족 예외를 400 에러 응답으로 변환한다")
    void handleInsufficientMemberPoint() {
        ResponseEntity<ErrorResponse> response = handler.handleInsufficientMemberPoint(
            new InsufficientMemberPointException()
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().code()).isEqualTo("MEMBER.INSUFFICIENT_POINT");
        assertThat(response.getBody().message()).isEqualTo("포인트가 부족합니다.");
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

    @Test
    @DisplayName("옵션 상품 미존재 예외를 404 에러 응답으로 변환한다")
    void handleOptionProductNotFound() {
        ResponseEntity<ErrorResponse> response = handler.handleOptionProductNotFound(
            new OptionProductNotFoundException()
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().code()).isEqualTo("OPTION.PRODUCT_NOT_FOUND");
    }

    @Test
    @DisplayName("옵션 미존재 예외를 404 에러 응답으로 변환한다")
    void handleOptionNotFound() {
        ResponseEntity<ErrorResponse> response = handler.handleOptionNotFound(new OptionNotFoundException());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().code()).isEqualTo("OPTION.NOT_FOUND");
    }

    @Test
    @DisplayName("중복 옵션명 예외를 400 에러 응답으로 변환한다")
    void handleDuplicateOptionName() {
        ResponseEntity<ErrorResponse> response = handler.handleDuplicateOptionName(
            new DuplicateOptionNameException()
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().code()).isEqualTo("OPTION.DUPLICATE_NAME");
    }

    @Test
    @DisplayName("옵션 삭제 제한 예외를 400 에러 응답으로 변환한다")
    void handleOptionDeletionNotAllowed() {
        ResponseEntity<ErrorResponse> response = handler.handleOptionDeletionNotAllowed(
            new OptionDeletionNotAllowedException()
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().code()).isEqualTo("OPTION.DELETE_NOT_ALLOWED");
    }

    @Test
    @DisplayName("옵션명 검증 예외를 400 에러 응답으로 변환한다")
    void handleOptionValidation() {
        ResponseEntity<ErrorResponse> response = handler.handleOptionValidation(
            new OptionValidationException("옵션 이름은 필수입니다.")
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().code()).isEqualTo("OPTION.INVALID_NAME");
    }

    @Test
    @DisplayName("옵션 수량 예외를 400 에러 응답으로 변환한다")
    void handleOptionQuantity() {
        ResponseEntity<ErrorResponse> response = handler.handleOptionQuantity(
            new OptionQuantityException("차감 수량은 1 이상이어야 합니다.")
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().code()).isEqualTo("OPTION.INVALID_QUANTITY");
        assertThat(response.getBody().message()).isEqualTo("차감 수량은 1 이상이어야 합니다.");
    }

    @Test
    @DisplayName("주문 옵션 미존재 예외를 404 에러 응답으로 변환한다")
    void handleOrderOptionNotFound() {
        ResponseEntity<ErrorResponse> response = handler.handleOrderOptionNotFound(
            new OrderOptionNotFoundException(999999L)
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().code()).isEqualTo("ORDER.OPTION_NOT_FOUND");
        assertThat(response.getBody().message()).isEqualTo("주문할 옵션을 찾을 수 없습니다. optionId=999999");
    }

    @Test
    @DisplayName("주문 검증 예외를 400 에러 응답으로 변환한다")
    void handleOrderValidation() {
        ResponseEntity<ErrorResponse> response = handler.handleOrderValidation(
            new OrderValidationException("주문 수량은 1 이상이어야 합니다.")
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().code()).isEqualTo("ORDER.INVALID");
        assertThat(response.getBody().message()).isEqualTo("주문 수량은 1 이상이어야 합니다.");
    }

    @Test
    @DisplayName("상품 미존재 예외를 404 에러 응답으로 변환한다")
    void handleProductNotFound() {
        ResponseEntity<ErrorResponse> response = handler.handleProductNotFound(new ProductNotFoundException());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().code()).isEqualTo("PRODUCT.NOT_FOUND");
    }

    @Test
    @DisplayName("상품 카테고리 미존재 예외를 404 에러 응답으로 변환한다")
    void handleProductCategoryNotFound() {
        ResponseEntity<ErrorResponse> response = handler.handleProductCategoryNotFound(
            new ProductCategoryNotFoundException()
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().code()).isEqualTo("PRODUCT.CATEGORY_NOT_FOUND");
    }

    @Test
    @DisplayName("상품 검증 예외를 400 에러 응답으로 변환한다")
    void handleProductValidation() {
        ResponseEntity<ErrorResponse> response = handler.handleProductValidation(
            new ProductValidationException("상품 이름은 필수입니다.")
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().code()).isEqualTo("PRODUCT.INVALID_NAME");
        assertThat(response.getBody().message()).isEqualTo("상품 이름은 필수입니다.");
    }
}
