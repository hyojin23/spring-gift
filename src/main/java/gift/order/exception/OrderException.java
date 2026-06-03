package gift.order.exception;

public class OrderException extends RuntimeException {

    private final OrderErrorCode errorCode;

    public OrderException(OrderErrorCode errorCode, Object... args) {
        super(errorCode.getMessage(args));
        this.errorCode = errorCode;
    }

    public OrderErrorCode getErrorCode() {
        return errorCode;
    }
}
