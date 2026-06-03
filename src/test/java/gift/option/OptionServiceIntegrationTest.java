package gift.option;

import gift.category.Category;
import gift.category.CategoryRepository;
import gift.option.exception.OptionDeletionNotAllowedException;
import gift.product.Product;
import gift.product.ProductRepository;
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
class OptionServiceIntegrationTest {

    @Autowired
    private OptionService optionService;

    @Autowired
    private OptionRepository optionRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    @DisplayName("옵션 생성 결과를 DB에서 조회할 수 있다")
    void createOption() {
        Product product = saveProduct();

        OptionResponse response = optionService.createOption(product.getId(), new OptionRequest("basic", 10));
        flushAndClear();

        Option option = optionRepository.findById(response.id()).orElseThrow();
        assertThat(option.getProduct().getId()).isEqualTo(product.getId());
        assertThat(option.getName()).isEqualTo("basic");
        assertThat(option.getQuantity()).isEqualTo(10);
    }

    @Test
    @DisplayName("옵션 삭제 결과를 DB에서 확인할 수 있다")
    void deleteOption() {
        Product product = saveProduct();
        Option option = optionRepository.save(new Option(product, "basic", 10));
        optionRepository.save(new Option(product, "extra", 10));
        flushAndClear();

        optionService.deleteOption(product.getId(), option.getId());
        flushAndClear();

        assertThat(optionRepository.existsById(option.getId())).isFalse();
    }

    @Test
    @DisplayName("마지막 옵션은 삭제할 수 없다")
    void deleteLastOption() {
        Product product = saveProduct();
        Option option = optionRepository.save(new Option(product, "basic", 10));
        flushAndClear();

        assertThatThrownBy(() -> optionService.deleteOption(product.getId(), option.getId()))
            .isInstanceOf(OptionDeletionNotAllowedException.class);
    }

    @Test
    @DisplayName("주문 시 옵션 재고 차감 결과를 DB에서 조회할 수 있다")
    void decreaseQuantityForOrder() {
        Product product = saveProduct();
        Option option = optionRepository.save(new Option(product, "basic", 10));
        flushAndClear();

        optionService.decreaseQuantityForOrder(option.getId(), 3);
        flushAndClear();

        Option updated = optionRepository.findById(option.getId()).orElseThrow();
        assertThat(updated.getQuantity()).isEqualTo(7);
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