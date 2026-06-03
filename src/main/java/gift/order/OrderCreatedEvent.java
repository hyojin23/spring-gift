package gift.order;

public record OrderCreatedEvent(Long orderId, OrderNotificationPayload notification) {
}
