package gift.product;

import gift.category.Category;
import gift.product.exception.ProductValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProductTest {

    @Test
    @DisplayName("상품명이 비어 있으면 상품을 생성할 수 없다")
    void createBlankName() {
        Category category = category();

        assertThatThrownBy(() -> new Product(" ", 1000, "https://example.com/product.jpg", category))
            .isInstanceOf(ProductValidationException.class)
            .hasMessage("상품 이름은 필수입니다.");
    }

    @Test
    @DisplayName("가격이 0 이하이면 상품을 생성할 수 없다")
    void createNonPositivePrice() {
        Category category = category();

        assertThatThrownBy(() -> new Product("상품", 0, "https://example.com/product.jpg", category))
            .isInstanceOf(ProductValidationException.class)
            .hasMessage("상품 가격은 0보다 커야 합니다.");
    }

    @Test
    @DisplayName("이미지 URL이 비어 있으면 상품을 생성할 수 없다")
    void createBlankImageUrl() {
        Category category = category();

        assertThatThrownBy(() -> new Product("상품", 1000, " ", category))
            .isInstanceOf(ProductValidationException.class)
            .hasMessage("상품 이미지 URL은 필수입니다.");
    }

    @Test
    @DisplayName("카테고리가 없으면 상품을 생성할 수 없다")
    void createNullCategory() {
        assertThatThrownBy(() -> new Product("상품", 1000, "https://example.com/product.jpg", null))
            .isInstanceOf(ProductValidationException.class)
            .hasMessage("상품 카테고리는 필수입니다.");
    }

    @Test
    @DisplayName("유효하지 않은 값으로 상품을 수정할 수 없다")
    void updateInvalidValue() {
        Product product = product();
        Category category = category();

        assertThatThrownBy(() -> product.update("상품", -1, "https://example.com/product.jpg", category))
            .isInstanceOf(ProductValidationException.class)
            .hasMessage("상품 가격은 0보다 커야 합니다.");
    }

    @Test
    @DisplayName("상품 수정 검증에 실패하면 기존 상태를 유지한다")
    void updateInvalidValueKeepOriginalState() {
        Product product = product();
        Category originalCategory = product.getCategory();
        Category newCategory = new Category("새 카테고리", "#000000", "https://example.com/new-category.jpg", "설명");

        assertThatThrownBy(() -> product.update("수정상품", 0, "https://example.com/updated.jpg", newCategory))
            .isInstanceOf(ProductValidationException.class);

        assertThat(product.getName()).isEqualTo("상품");
        assertThat(product.getPrice()).isEqualTo(1000);
        assertThat(product.getImageUrl()).isEqualTo("https://example.com/product.jpg");
        assertThat(product.getCategory()).isSameAs(originalCategory);
    }

    private Product product() {
        return new Product("상품", 1000, "https://example.com/product.jpg", category());
    }

    private Category category() {
        return new Category("카테고리", "#FFFFFF", "https://example.com/category.jpg", "설명");
    }
}
