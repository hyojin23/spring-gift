package gift.product;

import gift.category.Category;
import gift.category.CategoryRepository;
import gift.product.exception.ProductCategoryNotFoundException;
import gift.product.exception.ProductNotFoundException;
import gift.product.exception.ProductValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ProductServiceTest {

    private final ProductRepository productRepository = mock(ProductRepository.class);
    private final CategoryRepository categoryRepository = mock(CategoryRepository.class);
    private final ProductUseCaseService productUseCaseService = new ProductUseCaseService(
        productRepository,
        categoryRepository
    );
    private final ProductService productService = new ProductService(
        productRepository,
        productUseCaseService
    );

    @Test
    @DisplayName("상품을 찾지 못하면 상품 미존재 예외를 던진다")
    void getProductNotFound() {
        when(productRepository.findById(999999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.getProduct(999999L))
            .isInstanceOf(ProductNotFoundException.class);
    }

    @Test
    @DisplayName("존재하지 않는 카테고리로 상품을 생성하면 상품 카테고리 미존재 예외를 던진다")
    void createProductCategoryNotFound() {
        ProductRequest request = new ProductRequest("상품", 1000, "https://example.com/product.jpg", 999999L);
        when(categoryRepository.findById(999999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.createProduct(request))
            .isInstanceOf(ProductCategoryNotFoundException.class);
    }

    @Test
    @DisplayName("상품명 검증에 실패하면 상품 검증 예외를 던진다")
    void createProductInvalidName() {
        Category category = category(1L);
        ProductRequest request = new ProductRequest("카카오 상품", 1000, "https://example.com/product.jpg", 1L);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        assertThatThrownBy(() -> productService.createProduct(request))
            .isInstanceOf(ProductValidationException.class);
    }

    @Test
    @DisplayName("존재하지 않는 상품을 수정하면 상품 미존재 예외를 던진다")
    void updateProductNotFound() {
        Category category = category(1L);
        ProductRequest request = new ProductRequest("상품", 1000, "https://example.com/product.jpg", 1L);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(productRepository.findById(999999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.updateProduct(999999L, request))
            .isInstanceOf(ProductNotFoundException.class);
    }

    private Category category(Long id) {
        Category category = new Category("카테고리", "#FFFFFF", "https://example.com/category.jpg", "설명");
        ReflectionTestUtils.setField(category, "id", id);
        return category;
    }
}
