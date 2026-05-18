package gift.order;

import gift.category.Category;
import gift.member.Member;
import gift.member.MemberRepository;
import gift.option.Option;
import gift.option.OptionRepository;
import gift.product.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class OrderServiceTest {

    private final OrderRepository orderRepository = mock(OrderRepository.class);
    private final OptionRepository optionRepository = mock(OptionRepository.class);
    private final MemberRepository memberRepository = mock(MemberRepository.class);
    private final KakaoMessageClient kakaoMessageClient = mock(KakaoMessageClient.class);
    private final OrderService orderService = new OrderService(
        orderRepository,
        optionRepository,
        memberRepository,
        kakaoMessageClient
    );

    @Test
    @DisplayName("회원의 주문 목록을 조회한다")
    void getOrders() {
        Option option = option();
        Order order = order(option, 1L, 1);
        when(orderRepository.findByMemberId(1L, PageRequest.of(0, 10)))
            .thenReturn(new PageImpl<>(List.of(order)));

        var result = orderService.getOrders(1L, PageRequest.of(0, 10));

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst().optionId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("주문 생성 시 재고와 포인트를 차감하고 주문을 저장한다")
    void createOrder() {
        Member member = member();
        member.chargePoint(10_000);
        Option option = option();
        OrderRequest request = new OrderRequest(1L, 2, "선물 메시지");
        Order saved = order(option, 1L, 2);
        when(optionRepository.findById(1L)).thenReturn(Optional.of(option));
        when(orderRepository.save(any(Order.class))).thenReturn(saved);

        Optional<OrderResponse> response = orderService.createOrder(member, request);

        assertThat(response).isPresent();
        assertThat(response.get().optionId()).isEqualTo(1L);
        assertThat(option.getQuantity()).isEqualTo(8);
        assertThat(member.getPoint()).isEqualTo(8_000);
        verify(optionRepository).save(option);
        verify(memberRepository).save(member);
    }

    @Test
    @DisplayName("존재하지 않는 옵션으로 주문하면 빈 결과를 반환한다")
    void createOrderOptionNotFound() {
        Member member = member();
        OrderRequest request = new OrderRequest(999999L, 1, "선물 메시지");
        when(optionRepository.findById(999999L)).thenReturn(Optional.empty());

        Optional<OrderResponse> response = orderService.createOrder(member, request);

        assertThat(response).isEmpty();
    }

    @Test
    @DisplayName("카카오 메시지 발송에 실패해도 주문 생성은 성공한다")
    void createOrderKakaoMessageFailure() {
        Member member = member();
        member.chargePoint(10_000);
        member.updateKakaoAccessToken("kakao-token");
        Option option = option();
        OrderRequest request = new OrderRequest(1L, 1, "선물 메시지");
        Order saved = order(option, 1L, 1);
        when(optionRepository.findById(1L)).thenReturn(Optional.of(option));
        when(orderRepository.save(any(Order.class))).thenReturn(saved);
        doThrow(new RuntimeException("kakao failure"))
            .when(kakaoMessageClient).sendToMe("kakao-token", saved, option.getProduct());

        assertThatCode(() -> orderService.createOrder(member, request))
            .doesNotThrowAnyException();
    }

    private Member member() {
        Member member = new Member("member@example.com", "password");
        ReflectionTestUtils.setField(member, "id", 1L);
        return member;
    }

    private Option option() {
        Product product = new Product("상품", 1000, "https://example.com/product.jpg", category());
        ReflectionTestUtils.setField(product, "id", 1L);
        Option option = new Option(product, "옵션", 10);
        ReflectionTestUtils.setField(option, "id", 1L);
        return option;
    }

    private Order order(Option option, Long memberId, int quantity) {
        Order order = new Order(option, memberId, quantity, "선물 메시지");
        ReflectionTestUtils.setField(order, "id", 1L);
        return order;
    }

    private Category category() {
        return new Category("카테고리", "#FFFFFF", "https://example.com/category.jpg", "설명");
    }
}
