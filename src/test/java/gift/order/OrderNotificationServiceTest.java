package gift.order;

import gift.category.Category;
import gift.member.Member;
import gift.option.Option;
import gift.product.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

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
        Member member = member();
        Option option = option();
        Order order = order(option, member.getId(), 1);

        orderNotificationService.sendOrderCreatedMessage(member, order, option);

        verifyNoInteractions(kakaoMessageClient);
    }

    @Test
    @DisplayName("카카오 access token이 있으면 메시지를 발송한다")
    void sendOrderCreatedMessage() {
        Member member = member();
        member.updateKakaoAccessToken("kakao-token");
        Option option = option();
        Order order = order(option, member.getId(), 1);

        orderNotificationService.sendOrderCreatedMessage(member, order, option);

        verify(kakaoMessageClient).sendToMe("kakao-token", order, option.getProduct());
    }

    @Test
    @DisplayName("카카오 메시지 발송에 실패해도 예외를 전파하지 않는다")
    void sendOrderCreatedMessageKakaoFailure() {
        Member member = member();
        member.updateKakaoAccessToken("kakao-token");
        Option option = option();
        Order order = order(option, member.getId(), 1);
        doThrow(new RuntimeException("kakao failure"))
            .when(kakaoMessageClient).sendToMe("kakao-token", order, option.getProduct());

        assertThatCode(() -> orderNotificationService.sendOrderCreatedMessage(member, order, option))
            .doesNotThrowAnyException();
    }

    private Member member() {
        Member member = new Member("member@example.com", "password");
        ReflectionTestUtils.setField(member, "id", 1L);
        return member;
    }

    private Option option() {
        Product product = new Product("상품", 1000, "https://example.com/product.jpg", category());
        ReflectionTestUtils.setField(product, "id", 1L);
        Option option = new Option(product, "옵션", 10);
        ReflectionTestUtils.setField(option, "id", 1L);
        return option;
    }

    private Order order(Option option, Long memberId, int quantity) {
        Order order = new Order(option, memberId, quantity, "선물 메시지");
        ReflectionTestUtils.setField(order, "id", 1L);
        return order;
    }

    private Category category() {
        return new Category("카테고리", "#FFFFFF", "https://example.com/category.jpg", "설명");
    }
}
