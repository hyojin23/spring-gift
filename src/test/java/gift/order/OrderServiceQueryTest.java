package gift.order;

import gift.category.Category;
import gift.category.CategoryRepository;
import gift.member.Member;
import gift.member.MemberRepository;
import gift.option.Option;
import gift.option.OptionRepository;
import gift.product.Product;
import gift.product.ProductRepository;
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
class OrderServiceQueryTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OptionRepository optionRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    @Test
    @DisplayName("주문 목록 조회 시 option과 product 추가 조회가 발생하지 않는다")
    void getOrdersDoesNotLoadOptionAndProduct() {
        Member member = memberRepository.save(new Member("order-query@example.com", "password"));
        Category category = categoryRepository.save(
            new Category("category", "#FFFFFF", "https://example.com/category.jpg", "description")
        );
        Product firstProduct = productRepository.save(
            new Product("product1", 1_000, "https://example.com/product1.jpg", category)
        );
        Product secondProduct = productRepository.save(
            new Product("product2", 2_000, "https://example.com/product2.jpg", category)
        );
        Option firstOption = optionRepository.save(new Option(firstProduct, "option1", 10));
        Option secondOption = optionRepository.save(new Option(secondProduct, "option2", 10));
        orderRepository.save(new Order(firstOption, member.getId(), 1, "message1"));
        orderRepository.save(new Order(secondOption, member.getId(), 1, "message2"));
        flushAndClear();

        Statistics statistics = entityManagerFactory.unwrap(SessionFactory.class).getStatistics();
        statistics.clear();

        orderService.getOrders(member.getId(), PageRequest.of(0, 20)).getContent();

        assertThat(statistics.getPrepareStatementCount()).isEqualTo(1);
    }

    private void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }
}