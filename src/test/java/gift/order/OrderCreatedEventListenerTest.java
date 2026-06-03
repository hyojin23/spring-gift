package gift.order;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

class OrderCreatedEventListenerTest {

    private final OrderNotificationService orderNotificationService = mock(OrderNotificationService.class);
    private final OrderCreatedEventListener listener = new OrderCreatedEventListener(orderNotificationService);

    @Test
    @DisplayName("주문 생성 이벤트를 받으면 주문 알림 발송을 위임한다")
    void handle() {
        OrderNotificationPayload payload = payload();

        listener.handle(new OrderCreatedEvent(1L, payload));

        verify(orderNotificationService).sendOrderCreatedMessage(1L, payload);
    }

    @Test
    @DisplayName("주문 알림 발송에 실패해도 이벤트 처리를 중단하지 않는다")
    void handleNotificationFailure() {
        OrderNotificationPayload payload = payload();
        doThrow(new RuntimeException("notification failure"))
            .when(orderNotificationService)
            .sendOrderCreatedMessage(1L, payload);

        listener.handle(new OrderCreatedEvent(1L, payload));

        verify(orderNotificationService).sendOrderCreatedMessage(1L, payload);
    }

    private OrderNotificationPayload payload() {
        return new OrderNotificationPayload("kakao-token", "상품", "옵션", 1000, 1, "선물 메시지");
    }
}
