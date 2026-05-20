package gift.order;

import gift.member.Member;
import gift.option.Option;
import org.springframework.stereotype.Service;

@Service
public class OrderNotificationService {

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
        } catch (Exception ignored) {
        }
    }
}
