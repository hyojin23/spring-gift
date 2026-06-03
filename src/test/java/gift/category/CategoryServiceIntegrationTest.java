package gift.category;

import gift.category.exception.CategoryNotFoundException;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class CategoryServiceIntegrationTest {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    @DisplayName("카테고리 생성 결과를 DB에서 조회할 수 있다")
    void createCategory() {
        CategoryResponse response = categoryService.createCategory(
            new CategoryRequest("카테고리", "#FFFFFF", "https://example.com/category.jpg", "설명")
        );

        flushAndClear();

        Category category = categoryRepository.findById(response.id()).orElseThrow();
        assertThat(category.getName()).isEqualTo("카테고리");
        assertThat(category.getColor()).isEqualTo("#FFFFFF");
    }

    @Test
    @DisplayName("카테고리 수정 결과를 DB에서 조회할 수 있다")
    void updateCategory() {
        Category saved = categoryRepository.save(
            new Category("카테고리", "#FFFFFF", "https://example.com/category.jpg", "설명")
        );
        flushAndClear();

        Category category = categoryService.findCategory(saved.getId());
        categoryService.updateCategory(
            category,
            new CategoryRequest("수정 카테고리", "#000000", "https://example.com/updated.jpg", "수정 설명")
        );
        flushAndClear();

        Category updated = categoryRepository.findById(saved.getId()).orElseThrow();
        assertThat(updated.getName()).isEqualTo("수정 카테고리");
        assertThat(updated.getColor()).isEqualTo("#000000");
        assertThat(updated.getImageUrl()).isEqualTo("https://example.com/updated.jpg");
    }

    @Test
    @DisplayName("카테고리 삭제 결과를 DB에서 확인할 수 있다")
    void deleteCategory() {
        Category saved = categoryRepository.save(
            new Category("카테고리", "#FFFFFF", "https://example.com/category.jpg", "설명")
        );
        flushAndClear();

        categoryService.deleteCategory(saved.getId());
        flushAndClear();

        assertThat(categoryRepository.existsById(saved.getId())).isFalse();
    }

    @Test
    @DisplayName("존재하지 않는 카테고리를 조회하면 예외가 발생한다")
    void findCategoryNotFound() {
        assertThatThrownBy(() -> categoryService.findCategory(999999L))
            .isInstanceOf(CategoryNotFoundException.class);
    }

    private void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }
}