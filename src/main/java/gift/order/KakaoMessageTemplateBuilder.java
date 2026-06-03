package gift.order;

import org.springframework.stereotype.Component;

@Component
public class KakaoMessageTemplateBuilder {

    public String build(OrderNotificationPayload payload) {
        String totalPrice = String.format("%,d", payload.productPrice() * payload.quantity());
        String message = payload.message() != null && !payload.message().isBlank()
            ? "\\n\\n💌 " + payload.message()
            : "";
        return """
            {
                "object_type": "text",
                "text": "🎁 선물이 도착했어요!\\n\\n%s (%s)\\n수량: %d개\\n금액: %s원%s",
                "link": {},
                "button_title": "선물 확인하기"
            }
            """.formatted(
            payload.productName(),
            payload.optionName(),
            payload.quantity(),
            totalPrice,
            message
        );
    }
}
