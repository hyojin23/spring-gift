package gift.order.exception;

import org.springframework.http.HttpStatus;

public enum OrderErrorCode {
    OPTION_NOT_FOUND(HttpStatus.NOT_FOUND, "ORDER.OPTION_NOT_FOUND", "주문 옵션을 찾을 수 없습니다. optionId=%s"),
    OPTION_REQUIRED(HttpStatus.BAD_REQUEST, "ORDER.INVALID", "주문 옵션은 필수입니다."),
    MEMBER_ID_REQUIRED(HttpStatus.BAD_REQUEST, "ORDER.INVALID", "주문 회원 ID는 필수입니다."),
    INVALID_QUANTITY(HttpStatus.BAD_REQUEST, "ORDER.INVALID", "주문 수량은 1 이상이어야 합니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;

    OrderErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getCode() {
        return code;
    }

    public String getMessage(Object... args) {
        if (args == null || args.length == 0) {
            return message;
        }
        return message.formatted(args);
    }
}
