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

    @Autowired
    private OrderRepository orderRepository;

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

    @Test
    @DisplayName("같은 회원이 서로 다른 옵션을 동시에 주문해도 포인트를 초과해 차감하지 않는다")
    void createOrdersForSameMemberConcurrently() throws InterruptedException {
        Member savedMember = saveMemberWithPoint(10_000);
        List<Option> options = saveOptions(ORDER_COUNT, 1);
        List<Member> detachedMembers = loadDetachedMembers(savedMember.getId(), ORDER_COUNT);
        ExecutorService executorService = Executors.newFixedThreadPool(ORDER_COUNT);
        CountDownLatch readyLatch = new CountDownLatch(ORDER_COUNT);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(ORDER_COUNT);
        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failureCount = new AtomicInteger();

        for (int i = 0; i < ORDER_COUNT; i++) {
            Member member = detachedMembers.get(i);
            Option option = options.get(i);
            executorService.submit(() -> {
                readyLatch.countDown();
                try {
                    startLatch.await();
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

        Member updatedMember = memberRepository.findById(savedMember.getId()).orElseThrow();
        assertThat(successCount.get()).isEqualTo(10);
        assertThat(failureCount.get()).isEqualTo(ORDER_COUNT - 10);
        assertThat(updatedMember.getPoint()).isZero();
        assertThat(orderRepository.findByMemberId(
            savedMember.getId(),
            org.springframework.data.domain.Pageable.unpaged()
        ).getContent()).hasSize(10);
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

    private List<Option> saveOptions(int count, int quantity) {
        List<Option> options = new ArrayList<>();
        String suffix = UUID.randomUUID().toString();
        Category category = categoryRepository.save(new Category(
            "동시성 카테고리 " + suffix,
            "#FFFFFF",
            "https://example.com/category.jpg",
            "동시성 테스트"
        ));
        for (int i = 0; i < count; i++) {
            Product product = productRepository.save(new Product(
                "상품" + i,
                1_000,
                "https://example.com/product.jpg",
                category
            ));
            options.add(optionRepository.save(new Option(product, "옵션" + i, quantity)));
        }
        return options;
    }

    private Member saveMemberWithPoint(int point) {
        Member member = new Member("same-member-" + UUID.randomUUID() + "@example.com", "password");
        member.chargePoint(point);
        return memberRepository.save(member);
    }

    private List<Member> loadDetachedMembers(Long memberId, int count) {
        List<Member> members = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            members.add(memberRepository.findById(memberId).orElseThrow());
        }
        return members;
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
