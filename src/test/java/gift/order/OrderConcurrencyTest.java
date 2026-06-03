package gift.order;

import gift.category.Category;
import gift.category.CategoryRepository;
import gift.member.Member;
import gift.member.MemberRepository;
import gift.option.Option;
import gift.option.OptionRepository;
import gift.product.Product;
import gift.product.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class OrderConcurrencyTest {

    private static final int ORDER_COUNT = 50;

    @Autowired
    private OrderService orderService;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OptionRepository optionRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("같은 옵션에 주문이 동시에 들어와도 재고를 정확히 차감한다")
    void createOrderConcurrently() throws InterruptedException {
        Option option = saveOptionWithQuantity(ORDER_COUNT);
        List<Long> memberIds = saveMembers(ORDER_COUNT);
        ExecutorService executorService = Executors.newFixedThreadPool(ORDER_COUNT);
        CountDownLatch readyLatch = new CountDownLatch(ORDER_COUNT);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(ORDER_COUNT);
        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failureCount = new AtomicInteger();

        for (Long memberId : memberIds) {
            executorService.submit(() -> {
                readyLatch.countDown();
                try {
                    startLatch.await();
                    Member member = memberRepository.findById(memberId).orElseThrow();
                    orderService.createOrder(member, new OrderRequest(option.getId(), 1, "동시 주문"));
                    successCount.incrementAndGet();
                } catch (Exception exception) {
                    failureCount.incrementAndGet();
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        try {
            assertThat(readyLatch.await(10, TimeUnit.SECONDS)).isTrue();
            startLatch.countDown();
            assertThat(doneLatch.await(30, TimeUnit.SECONDS)).isTrue();
        } finally {
            executorService.shutdownNow();
        }

        Option updatedOption = optionRepository.findById(option.getId()).orElseThrow();
        assertThat(successCount.get()).isEqualTo(ORDER_COUNT);
        assertThat(failureCount.get()).isZero();
        assertThat(updatedOption.getQuantity()).isZero();
    }

    private Option saveOptionWithQuantity(int quantity) {
        String suffix = UUID.randomUUID().toString();
        Category category = categoryRepository.save(new Category(
            "동시성 카테고리 " + suffix,
            "#FFFFFF",
            "https://example.com/category.jpg",
            "동시성 테스트"
        ));
        Product product = productRepository.save(new Product(
            "동시성 상품",
            1_000,
            "https://example.com/product.jpg",
            category
        ));
        return optionRepository.save(new Option(product, "동시성 옵션", quantity));
    }

    private List<Long> saveMembers(int count) {
        List<Long> memberIds = new ArrayList<>();
        String suffix = UUID.randomUUID().toString();
        for (int i = 0; i < count; i++) {
            Member member = new Member("order-concurrency-" + i + "-" + suffix + "@example.com", "password");
            member.chargePoint(10_000);
            memberIds.add(memberRepository.save(member).getId());
        }
        return memberIds;
    }
}
