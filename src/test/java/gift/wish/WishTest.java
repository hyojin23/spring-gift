package gift.wish;

import gift.category.Category;
import gift.product.Product;
import gift.wish.exception.WishValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class WishTest {

    @Test
    @DisplayName("위시를 생성한다")
    void create() {
        Product product = product();

        Wish wish = new Wish(1L, product);

        assertThat(wish.getMemberId()).isEqualTo(1L);
        assertThat(wish.getProduct()).isEqualTo(product);
    }

    @Test
    @DisplayName("회원 id는 필수이다")
    void createWithNullMemberId() {
        assertThatThrownBy(() -> new Wish(null, product()))
            .isInstanceOf(WishValidationException.class)
            .hasMessage("회원 id는 필수입니다.");
    }

    @Test
    @DisplayName("위시 상품은 필수이다")
    void createWithNullProduct() {
        assertThatThrownBy(() -> new Wish(1L, null))
            .isInstanceOf(WishValidationException.class)
            .hasMessage("위시 상품은 필수입니다.");
    }

    private Product product() {
        return new Product("상품", 10_000, "https://example.com/product.jpg", category());
    }

    private Category category() {
        return new Category("카테고리", "#FFFFFF", "https://example.com/category.jpg", "설명");
    }
}
