package gift.category;

import gift.category.exception.CategoryValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CategoryTest {

    @Test
    @DisplayName("카테고리를 생성한다")
    void create() {
        Category category = category();

        assertThat(category.getName()).isEqualTo("카테고리");
        assertThat(category.getColor()).isEqualTo("#FFFFFF");
        assertThat(category.getImageUrl()).isEqualTo("https://example.com/category.jpg");
        assertThat(category.getDescription()).isEqualTo("설명");
    }

    @Test
    @DisplayName("카테고리 이름은 필수이다")
    void createWithBlankName() {
        assertThatThrownBy(() -> new Category(" ", "#FFFFFF", "https://example.com/category.jpg", "설명"))
            .isInstanceOf(CategoryValidationException.class)
            .hasMessage("카테고리 이름은 필수입니다.");
    }

    @Test
    @DisplayName("카테고리 색상은 필수이다")
    void createWithBlankColor() {
        assertThatThrownBy(() -> new Category("카테고리", " ", "https://example.com/category.jpg", "설명"))
            .isInstanceOf(CategoryValidationException.class)
            .hasMessage("카테고리 색상은 필수입니다.");
    }

    @Test
    @DisplayName("카테고리 이미지 URL은 필수이다")
    void createWithBlankImageUrl() {
        assertThatThrownBy(() -> new Category("카테고리", "#FFFFFF", " ", "설명"))
            .isInstanceOf(CategoryValidationException.class)
            .hasMessage("카테고리 이미지 URL은 필수입니다.");
    }

    @Test
    @DisplayName("카테고리를 수정한다")
    void update() {
        Category category = category();

        category.update("수정 카테고리", "#000000", "https://example.com/updated.jpg", "수정 설명");

        assertThat(category.getName()).isEqualTo("수정 카테고리");
        assertThat(category.getColor()).isEqualTo("#000000");
        assertThat(category.getImageUrl()).isEqualTo("https://example.com/updated.jpg");
        assertThat(category.getDescription()).isEqualTo("수정 설명");
    }

    @Test
    @DisplayName("카테고리 수정 시 이름, 색상, 이미지 URL은 필수이다")
    void updateWithInvalidValues() {
        Category category = category();

        assertThatThrownBy(() -> category.update(" ", "#000000", "https://example.com/updated.jpg", "설명"))
            .isInstanceOf(CategoryValidationException.class)
            .hasMessage("카테고리 이름은 필수입니다.");
        assertThatThrownBy(() -> category.update("수정 카테고리", " ", "https://example.com/updated.jpg", "설명"))
            .isInstanceOf(CategoryValidationException.class)
            .hasMessage("카테고리 색상은 필수입니다.");
        assertThatThrownBy(() -> category.update("수정 카테고리", "#000000", " ", "설명"))
            .isInstanceOf(CategoryValidationException.class)
            .hasMessage("카테고리 이미지 URL은 필수입니다.");
    }

    private Category category() {
        return new Category("카테고리", "#FFFFFF", "https://example.com/category.jpg", "설명");
    }
}
