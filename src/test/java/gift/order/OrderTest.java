package gift.order;

import gift.category.Category;
import gift.option.Option;
import gift.order.exception.OrderValidationException;
import gift.product.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OrderTest {

    @Test
    @DisplayName("유효한 값으로 주문을 생성한다")
    void createOrder() {
        Option option = option();

        Order order = new Order(option, 1L, 1, "선물 메시지");

        assertThat(order.getOption()).isEqualTo(option);
        assertThat(order.getMemberId()).isEqualTo(1L);
        assertThat(order.getQuantity()).isEqualTo(1);
        assertThat(order.getMessage()).isEqualTo("선물 메시지");
        assertThat(order.getOrderDateTime()).isNotNull();
    }

    @Test
    @DisplayName("메시지가 없어도 주문을 생성한다")
    void createOrderWithoutMessage() {
        Order order = new Order(option(), 1L, 1, null);

        assertThat(order.getMessage()).isNull();
        assertThat(order.getOrderDateTime()).isNotNull();
    }

    @Test
    @DisplayName("옵션이 없으면 주문을 생성할 수 없다")
    void createOrderWithoutOption() {
        assertThatThrownBy(() -> new Order(null, 1L, 1, "선물 메시지"))
            .isInstanceOf(OrderValidationException.class)
            .hasMessage("주문 옵션은 필수입니다.");
    }

    @Test
    @DisplayName("회원 ID가 없으면 주문을 생성할 수 없다")
    void createOrderWithoutMemberId() {
        assertThatThrownBy(() -> new Order(option(), null, 1, "선물 메시지"))
            .isInstanceOf(OrderValidationException.class)
            .hasMessage("주문 회원 ID는 필수입니다.");
    }

    @Test
    @DisplayName("주문 수량이 0이면 주문을 생성할 수 없다")
    void createOrderWithZeroQuantity() {
        assertThatThrownBy(() -> new Order(option(), 1L, 0, "선물 메시지"))
            .isInstanceOf(OrderValidationException.class)
            .hasMessage("주문 수량은 1 이상이어야 합니다.");
    }

    @Test
    @DisplayName("주문 수량이 음수이면 주문을 생성할 수 없다")
    void createOrderWithNegativeQuantity() {
        assertThatThrownBy(() -> new Order(option(), 1L, -1, "선물 메시지"))
            .isInstanceOf(OrderValidationException.class)
            .hasMessage("주문 수량은 1 이상이어야 합니다.");
    }

    private Option option() {
        Product product = new Product("상품", 1000, "https://example.com/product.jpg", category());
        ReflectionTestUtils.setField(product, "id", 1L);
        Option option = new Option(product, "옵션", 10);
        ReflectionTestUtils.setField(option, "id", 1L);
        return option;
    }

    private Category category() {
        return new Category("카테고리", "#FFFFFF", "https://example.com/category.jpg", "설명");
    }
}
