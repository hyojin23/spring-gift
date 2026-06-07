package gift.category;

import gift.category.exception.CategoryDeletionNotAllowedException;
import gift.product.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CategoryDeletionPolicyTest {

    private final ProductRepository productRepository = mock(ProductRepository.class);
    private final CategoryDeletionPolicy categoryDeletionPolicy = new CategoryDeletionPolicy(productRepository);

    @Test
    @DisplayName("상품이 있으면 카테고리를 삭제할 수 없다")
    void validateDeletableWithProducts() {
        when(productRepository.existsByCategoryId(1L)).thenReturn(true);

        assertThatThrownBy(() -> categoryDeletionPolicy.validateDeletable(1L))
            .isInstanceOf(CategoryDeletionNotAllowedException.class);
    }

    @Test
    @DisplayName("카테고리 삭제 가능 여부를 상품 참조로 확인한다")
    void validateDeletable() {
        when(productRepository.existsByCategoryId(1L)).thenReturn(false);

        categoryDeletionPolicy.validateDeletable(1L);

        verify(productRepository).existsByCategoryId(1L);
    }
}
