package gift.order;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class OrderNotificationService {
    private static final Logger log = LoggerFactory.getLogger(OrderNotificationService.class);

    private final KakaoMessageClient kakaoMessageClient;

    public OrderNotificationService(KakaoMessageClient kakaoMessageClient) {
        this.kakaoMessageClient = kakaoMessageClient;
    }

    public void sendOrderCreatedMessage(Long orderId, OrderNotificationPayload payload) {
        if (payload.kakaoAccessToken() == null) {
            return;
        }
        try {
            kakaoMessageClient.sendToMe(payload.kakaoAccessToken(), payload);
        } catch (Exception exception) {
            log.warn("카카오 주문 메시지 발송에 실패했습니다. orderId={}", orderId, exception);
        }
    }
}
