package gift.order;

import gift.category.Category;
import gift.member.Member;
import gift.option.Option;
import gift.product.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class OrderCreatedEventListenerTest {

    private final OrderNotificationService orderNotificationService = mock(OrderNotificationService.class);
    private final OrderCreatedEventListener listener = new OrderCreatedEventListener(orderNotificationService);

    @Test
    @DisplayName("주문 생성 이벤트를 받으면 주문 알림 발송을 위임한다")
    void handle() {
        Member member = member();
        Option option = option();
        Order order = order(option, member.getId(), 1);

        listener.handle(new OrderCreatedEvent(member, order, option));

        verify(orderNotificationService).sendOrderCreatedMessage(member, order, option);
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
