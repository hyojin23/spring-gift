package gift.wish;

import gift.category.Category;
import gift.category.CategoryRepository;
import gift.product.Product;
import gift.product.ProductRepository;
import gift.wish.exception.UnauthorizedWishAccessException;
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
class WishServiceIntegrationTest {

    @Autowired
    private WishService wishService;

    @Autowired
    private WishRepository wishRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    @DisplayName("위시 추가 결과를 DB에서 조회할 수 있다")
    void addWish() {
        Product product = saveProduct();

        WishAddResult result = wishService.addWish(1L, product.getId());
        flushAndClear();

        Wish wish = wishRepository.findById(result.response().id()).orElseThrow();
        assertThat(result.created()).isTrue();
        assertThat(wish.getMemberId()).isEqualTo(1L);
        assertThat(wish.getProduct().getId()).isEqualTo(product.getId());
    }

    @Test
    @DisplayName("이미 추가한 위시는 중복 저장하지 않는다")
    void addWishDuplicate() {
        Product product = saveProduct();
        Wish existing = wishRepository.save(new Wish(1L, product));
        flushAndClear();

        WishAddResult result = wishService.addWish(1L, product.getId());
        flushAndClear();

        assertThat(result.created()).isFalse();
        assertThat(result.response().id()).isEqualTo(existing.getId());
        assertThat(wishRepository.findByMemberIdAndProductId(1L, product.getId())).isPresent();
    }

    @Test
    @DisplayName("위시 삭제 결과를 DB에서 확인할 수 있다")
    void removeWish() {
        Product product = saveProduct();
        Wish wish = wishRepository.save(new Wish(1L, product));
        flushAndClear();

        wishService.removeWish(1L, wish.getId());
        flushAndClear();

        assertThat(wishRepository.existsById(wish.getId())).isFalse();
    }

    @Test
    @DisplayName("다른 회원의 위시는 삭제할 수 없다")
    void removeWishForbidden() {
        Product product = saveProduct();
        Wish wish = wishRepository.save(new Wish(2L, product));
        flushAndClear();

        assertThatThrownBy(() -> wishService.removeWish(1L, wish.getId()))
            .isInstanceOf(UnauthorizedWishAccessException.class);
    }

    @Test
    @DisplayName("회원과 상품으로 위시를 삭제할 수 있다")
    void removeWishByProduct() {
        Product product = saveProduct();
        Wish wish = wishRepository.save(new Wish(1L, product));
        flushAndClear();

        wishService.removeWishByProduct(1L, product.getId());
        flushAndClear();

        assertThat(wishRepository.existsById(wish.getId())).isFalse();
    }

    private Product saveProduct() {
        Category category = categoryRepository.save(
            new Category("category", "#FFFFFF", "https://example.com/category.jpg", "description")
        );
        return productRepository.save(new Product("product", 1_000, "https://example.com/product.jpg", category));
    }

    private void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }
}