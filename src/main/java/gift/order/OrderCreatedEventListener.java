package gift.order;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class OrderCreatedEventListener {
    private static final Logger log = LoggerFactory.getLogger(OrderCreatedEventListener.class);

    private final OrderNotificationService orderNotificationService;

    public OrderCreatedEventListener(OrderNotificationService orderNotificationService) {
        this.orderNotificationService = orderNotificationService;
    }

    @Async("orderEventTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(OrderCreatedEvent event) {
        try {
            orderNotificationService.sendOrderCreatedMessage(event.orderId(), event.notification());
        } catch (Exception exception) {
            log.warn("주문 생성 이벤트 처리에 실패했습니다. orderId={}", event.orderId(), exception);
        }
    }
}
