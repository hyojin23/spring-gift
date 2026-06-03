package gift.product;

import gift.category.Category;
import gift.category.CategoryRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = "spring.jpa.properties.hibernate.generate_statistics=true")
@Transactional
class ProductServiceQueryTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    @Test
    @DisplayName("상품 목록 조회 시 category 추가 조회가 발생하지 않는다")
    void getProductsDoesNotLoadCategory() {
        Category firstCategory = categoryRepository.save(
            new Category("category1", "#FFFFFF", "https://example.com/category1.jpg", "description")
        );
        Category secondCategory = categoryRepository.save(
            new Category("category2", "#000000", "https://example.com/category2.jpg", "description")
        );
        productRepository.save(new Product("product1", 1_000, "https://example.com/product1.jpg", firstCategory));
        productRepository.save(new Product("product2", 2_000, "https://example.com/product2.jpg", secondCategory));
        flushAndClear();

        Statistics statistics = entityManagerFactory.unwrap(SessionFactory.class).getStatistics();
        statistics.clear();

        productService.getProducts(PageRequest.of(0, 20)).getContent();

        assertThat(statistics.getPrepareStatementCount()).isEqualTo(1);
    }

    private void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }
}