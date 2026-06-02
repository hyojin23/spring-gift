package gift.option;

import gift.category.Category;
import gift.option.exception.OptionQuantityException;
import gift.option.exception.OptionValidationException;
import gift.product.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OptionTest {

    @Test
    @DisplayName("옵션명에 허용되지 않는 문자가 있으면 옵션을 생성할 수 없다")
    void createWithInvalidName() {
        Product product = product();

        assertThatThrownBy(() -> new Option(product, "옵션@", 10))
            .isInstanceOf(OptionValidationException.class);
    }

    @Test
    @DisplayName("수량이 0 이하이면 옵션을 생성할 수 없다")
    void createWithNonPositiveQuantity() {
        Product product = product();

        assertThatThrownBy(() -> new Option(product, "옵션", 0))
            .isInstanceOf(OptionQuantityException.class)
            .hasMessage("옵션 수량은 1 이상 99,999,999 이하이어야 합니다.");
    }

    @Test
    @DisplayName("수량이 최대값을 초과하면 옵션을 생성할 수 없다")
    void createWithTooLargeQuantity() {
        Product product = product();

        assertThatThrownBy(() -> new Option(product, "옵션", 100_000_000))
            .isInstanceOf(OptionQuantityException.class)
            .hasMessage("옵션 수량은 1 이상 99,999,999 이하이어야 합니다.");
    }

    @Test
    @DisplayName("정상 수량이면 옵션을 생성할 수 있다")
    void createWithValidQuantity() {
        Option option = new Option(product(), "옵션", 10);

        assertThat(option.getQuantity()).isEqualTo(10);
    }

    @Test
    @DisplayName("차감 수량이 0 이하이면 옵션 수량을 차감할 수 없다")
    void subtractNonPositiveQuantity() {
        Option option = new Option(product(), "옵션", 10);

        assertThatThrownBy(() -> option.subtractQuantity(0))
            .isInstanceOf(OptionQuantityException.class)
            .hasMessage("차감 수량은 1 이상이어야 합니다.");
    }

    @Test
    @DisplayName("현재 재고보다 큰 수량은 차감할 수 없다")
    void subtractMoreThanQuantity() {
        Option option = new Option(product(), "옵션", 10);

        assertThatThrownBy(() -> option.subtractQuantity(11))
            .isInstanceOf(OptionQuantityException.class)
            .hasMessage("차감할 수량이 현재 재고보다 많습니다.");
    }

    @Test
    @DisplayName("옵션 수량을 차감한다")
    void subtractQuantity() {
        Option option = new Option(product(), "옵션", 10);

        option.subtractQuantity(3);

        assertThat(option.getQuantity()).isEqualTo(7);
    }

    private Product product() {
        return new Product("상품", 1000, "https://example.com/product.jpg", category());
    }

    private Category category() {
        return new Category("카테고리", "#FFFFFF", "https://example.com/category.jpg", "설명");
    }
}
