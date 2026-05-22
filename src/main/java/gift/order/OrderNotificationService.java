package gift.order;

import gift.member.Member;
import gift.option.Option;
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

    public void sendOrderCreatedMessage(Member member, Order order, Option option) {
        if (member.getKakaoAccessToken() == null) {
            return;
        }
        try {
            kakaoMessageClient.sendToMe(member.getKakaoAccessToken(), order, option.getProduct());
        } catch (Exception exception) {
            log.warn("카카오 주문 메시지 발송에 실패했습니다. orderId={}", order.getId(), exception);
        }
    }
}
