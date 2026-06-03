package gift.order;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class KakaoMessageTemplateBuilderTest {

    private final KakaoMessageTemplateBuilder templateBuilder = new KakaoMessageTemplateBuilder();

    @Test
    @DisplayName("카카오 메시지 템플릿에 주문 상품 정보를 포함한다")
    void build() {
        OrderNotificationPayload payload = payload(123_000, 2, "선물 메시지");

        String template = templateBuilder.build(payload);

        assertThat(template).contains("\"object_type\": \"text\"");
        assertThat(template).contains("상품");
        assertThat(template).contains("옵션");
        assertThat(template).contains("수량: 2개");
        assertThat(template).contains("금액: 246,000원");
        assertThat(template).contains("\"button_title\": \"선물 확인하기\"");
    }

    @Test
    @DisplayName("주문 메시지가 있으면 카카오 메시지 템플릿에 포함한다")
    void buildWithMessage() {
        OrderNotificationPayload payload = payload(1000, 1, "선물 메시지");

        String template = templateBuilder.build(payload);

        assertThat(template).contains("💌 선물 메시지");
    }

    @Test
    @DisplayName("주문 메시지가 null이면 카카오 메시지 템플릿에서 메시지 영역을 생략한다")
    void buildWithoutMessage() {
        OrderNotificationPayload payload = payload(1000, 1, null);

        String template = templateBuilder.build(payload);

        assertThat(template).doesNotContain("💌");
    }

    @Test
    @DisplayName("주문 메시지가 blank이면 카카오 메시지 템플릿에서 메시지 영역을 생략한다")
    void buildWithBlankMessage() {
        OrderNotificationPayload payload = payload(1000, 1, "   ");

        String template = templateBuilder.build(payload);

        assertThat(template).doesNotContain("💌");
    }

    private OrderNotificationPayload payload(int productPrice, int quantity, String message) {
        return new OrderNotificationPayload("kakao-token", "상품", "옵션", productPrice, quantity, message);
    }
}
