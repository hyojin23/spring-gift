package gift.order;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

class OrderNotificationServiceTest {

    private final KakaoMessageClient kakaoMessageClient = mock(KakaoMessageClient.class);
    private final OrderNotificationService orderNotificationService = new OrderNotificationService(kakaoMessageClient);

    @Test
    @DisplayName("카카오 access token이 없으면 메시지를 발송하지 않는다")
    void sendOrderCreatedMessageWithoutKakaoAccessToken() {
        OrderNotificationPayload payload = payload(null);

        orderNotificationService.sendOrderCreatedMessage(1L, payload);

        verifyNoInteractions(kakaoMessageClient);
    }

    @Test
    @DisplayName("카카오 access token이 있으면 메시지를 발송한다")
    void sendOrderCreatedMessage() {
        OrderNotificationPayload payload = payload("kakao-token");

        orderNotificationService.sendOrderCreatedMessage(1L, payload);

        verify(kakaoMessageClient).sendToMe("kakao-token", payload);
    }

    @Test
    @DisplayName("카카오 메시지 발송에 실패해도 예외를 전파하지 않는다")
    void sendOrderCreatedMessageKakaoFailure() {
        OrderNotificationPayload payload = payload("kakao-token");
        doThrow(new RuntimeException("kakao failure"))
            .when(kakaoMessageClient).sendToMe("kakao-token", payload);

        assertThatCode(() -> orderNotificationService.sendOrderCreatedMessage(1L, payload))
            .doesNotThrowAnyException();
    }

    private OrderNotificationPayload payload(String kakaoAccessToken) {
        return new OrderNotificationPayload(kakaoAccessToken, "상품", "옵션", 1000, 1, "선물 메시지");
    }
}
