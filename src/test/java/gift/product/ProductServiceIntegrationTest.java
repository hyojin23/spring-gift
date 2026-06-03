package gift.product;

import gift.category.Category;
import gift.category.CategoryRepository;
import gift.product.exception.ProductCategoryNotFoundException;
import gift.product.exception.ProductNotFoundException;
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
class ProductServiceIntegrationTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    @DisplayName("상품 생성 결과를 DB에서 조회할 수 있다")
    void createProduct() {
        Category category = saveCategory();

        ProductResponse response = productService.createProduct(
            new ProductRequest("product", 1_000, "https://example.com/product.jpg", category.getId())
        );
        flushAndClear();

        Product product = productRepository.findById(response.id()).orElseThrow();
        assertThat(product.getName()).isEqualTo("product");
        assertThat(product.getCategory().getId()).isEqualTo(category.getId());
    }

    @Test
    @DisplayName("상품 수정 결과를 DB에서 조회할 수 있다")
    void updateProduct() {
        Category category = saveCategory();
        Product saved = productRepository.save(
            new Product("product", 1_000, "https://example.com/product.jpg", category)
        );
        flushAndClear();

        productService.updateProduct(
            saved.getId(),
            new ProductRequest("updated", 2_000, "https://example.com/updated.jpg", category.getId())
        );
        flushAndClear();

        Product updated = productRepository.findById(saved.getId()).orElseThrow();
        assertThat(updated.getName()).isEqualTo("updated");
        assertThat(updated.getPrice()).isEqualTo(2_000);
        assertThat(updated.getImageUrl()).isEqualTo("https://example.com/updated.jpg");
    }

    @Test
    @DisplayName("상품 삭제 결과를 DB에서 확인할 수 있다")
    void deleteProduct() {
        Category category = saveCategory();
        Product saved = productRepository.save(
            new Product("product", 1_000, "https://example.com/product.jpg", category)
        );
        flushAndClear();

        productService.deleteProduct(saved.getId());
        flushAndClear();

        assertThat(productRepository.existsById(saved.getId())).isFalse();
    }

    @Test
    @DisplayName("존재하지 않는 카테고리로 상품을 생성하면 예외가 발생한다")
    void createProductCategoryNotFound() {
        assertThatThrownBy(() -> productService.createProduct(
            new ProductRequest("product", 1_000, "https://example.com/product.jpg", 999999L)
        )).isInstanceOf(ProductCategoryNotFoundException.class);
    }

    @Test
    @DisplayName("존재하지 않는 상품을 수정하면 예외가 발생한다")
    void updateProductNotFound() {
        Category category = saveCategory();

        assertThatThrownBy(() -> productService.updateProduct(
            999999L,
            new ProductRequest("updated", 2_000, "https://example.com/updated.jpg", category.getId())
        )).isInstanceOf(ProductNotFoundException.class);
    }

    private Category saveCategory() {
        return categoryRepository.save(
            new Category("category", "#FFFFFF", "https://example.com/category.jpg", "description")
        );
    }

    private void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }
}