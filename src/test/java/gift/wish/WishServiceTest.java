package gift.wish;

import gift.category.Category;
import gift.product.ProductRepository;
import gift.product.Product;
import gift.wish.exception.UnauthorizedWishAccessException;
import gift.wish.exception.WishNotFoundException;
import gift.wish.exception.WishProductNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class WishServiceTest {

    private final WishRepository wishRepository = mock(WishRepository.class);
    private final ProductRepository productRepository = mock(ProductRepository.class);
    private final WishService wishService = new WishService(wishRepository, productRepository);

    @Test
    @DisplayName("상품을 찾지 못하면 위시 상품 미존재 예외를 던진다")
    void addWishProductNotFound() {
        when(productRepository.findById(999999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> wishService.addWish(1L, 999999L))
            .isInstanceOf(WishProductNotFoundException.class);
    }

    @Test
    @DisplayName("위시를 찾지 못하면 위시 미존재 예외를 던진다")
    void removeWishNotFound() {
        when(wishRepository.findById(999999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> wishService.removeWish(1L, 999999L))
            .isInstanceOf(WishNotFoundException.class);
    }

    @Test
    @DisplayName("다른 사용자의 위시를 삭제하려 하면 권한 예외를 던진다")
    void removeWishForbidden() {
        var wish = new Wish(2L, product());
        when(wishRepository.findById(1L)).thenReturn(Optional.of(wish));

        assertThatThrownBy(() -> wishService.removeWish(1L, 1L))
            .isInstanceOf(UnauthorizedWishAccessException.class);
    }

    private Product product() {
        return new Product("상품", 10_000, "https://example.com/product.jpg", category());
    }

    private Category category() {
        return new Category("카테고리", "#FFFFFF", "https://example.com/category.jpg", "설명");
    }
}
