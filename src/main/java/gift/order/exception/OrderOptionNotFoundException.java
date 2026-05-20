package gift.order.exception;

public class OrderOptionNotFoundException extends OrderException {

    public OrderOptionNotFoundException(Long optionId) {
        super("주문할 옵션을 찾을 수 없습니다. optionId=" + optionId);
    }
}
