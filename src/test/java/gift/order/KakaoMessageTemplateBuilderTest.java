package gift.order;

import gift.category.Category;
import gift.option.Option;
import gift.product.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

class KakaoMessageTemplateBuilderTest {

    private final KakaoMessageTemplateBuilder templateBuilder = new KakaoMessageTemplateBuilder();

    @Test
    @DisplayName("카카오 메시지 템플릿에 주문 상품 정보를 포함한다")
    void build() {
        Product product = product(123_000);
        Option option = option(product);
        Order order = order(option, 2, "선물 메시지");

        String template = templateBuilder.build(order, product);

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
        Product product = product(1000);
        Option option = option(product);
        Order order = order(option, 1, "선물 메시지");

        String template = templateBuilder.build(order, product);

        assertThat(template).contains("💌 선물 메시지");
    }

    @Test
    @DisplayName("주문 메시지가 null이면 카카오 메시지 템플릿에서 메시지 영역을 생략한다")
    void buildWithoutMessage() {
        Product product = product(1000);
        Option option = option(product);
        Order order = order(option, 1, null);

        String template = templateBuilder.build(order, product);

        assertThat(template).doesNotContain("💌");
    }

    @Test
    @DisplayName("주문 메시지가 blank이면 카카오 메시지 템플릿에서 메시지 영역을 생략한다")
    void buildWithBlankMessage() {
        Product product = product(1000);
        Option option = option(product);
        Order order = order(option, 1, "   ");

        String template = templateBuilder.build(order, product);

        assertThat(template).doesNotContain("💌");
    }

    private Order order(Option option, int quantity, String message) {
        Order order = new Order(option, 1L, quantity, message);
        ReflectionTestUtils.setField(order, "id", 1L);
        return order;
    }

    private Option option(Product product) {
        Option option = new Option(product, "옵션", 10);
        ReflectionTestUtils.setField(option, "id", 1L);
        return option;
    }

    private Product product(int price) {
        Product product = new Product("상품", price, "https://example.com/product.jpg", category());
        ReflectionTestUtils.setField(product, "id", 1L);
        return product;
    }

    private Category category() {
        return new Category("카테고리", "#FFFFFF", "https://example.com/category.jpg", "설명");
    }
}
