package gift.order;

import gift.category.Category;
import gift.member.Member;
import gift.member.MemberRepository;
import gift.member.exception.InsufficientMemberPointException;
import gift.option.Option;
import gift.option.OptionRepository;
import gift.option.exception.OptionQuantityException;
import gift.order.exception.OrderOptionNotFoundException;
import gift.product.Product;
import gift.wish.Wish;
import gift.wish.WishRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class OrderServiceTest {

    private final OrderRepository orderRepository = mock(OrderRepository.class);
    private final OptionRepository optionRepository = mock(OptionRepository.class);
    private final MemberRepository memberRepository = mock(MemberRepository.class);
    private final WishRepository wishRepository = mock(WishRepository.class);
    private final ApplicationEventPublisher eventPublisher = mock(ApplicationEventPublisher.class);
    private final OrderService orderService = new OrderService(
        orderRepository,
        optionRepository,
        memberRepository,
        wishRepository,
        eventPublisher
    );

    @Test
    @DisplayName("회원의 주문 목록을 조회한다")
    void getOrders() {
        Option option = option();
        Order order = order(option, 1L, 1);
        when(orderRepository.findByMemberId(1L, PageRequest.of(0, 10)))
            .thenReturn(new PageImpl<>(List.of(order)));

        Page<OrderResponse> result = orderService.getOrders(1L, PageRequest.of(0, 10));

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst().optionId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("주문 생성 시 재고와 포인트를 차감하고 주문을 저장한다")
    void createOrder() {
        Member member = member();
        member.chargePoint(10_000);
        Option option = option();
        Wish wish = new Wish(member.getId(), option.getProduct());
        OrderRequest request = new OrderRequest(1L, 2, "선물 메시지");
        Order saved = order(option, 1L, 2);
        when(optionRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(option));
        when(orderRepository.save(any(Order.class))).thenReturn(saved);
        when(wishRepository.findByMemberIdAndProductId(member.getId(), option.getProduct().getId()))
            .thenReturn(Optional.of(wish));

        OrderResponse response = orderService.createOrder(member, request);

        assertThat(response.optionId()).isEqualTo(1L);
        assertThat(option.getQuantity()).isEqualTo(8);
        assertThat(member.getPoint()).isEqualTo(8_000);
        verify(optionRepository).save(option);
        verify(memberRepository).save(member);
        verify(wishRepository).delete(wish);
        ArgumentCaptor<OrderCreatedEvent> eventCaptor = ArgumentCaptor.forClass(OrderCreatedEvent.class);
        verify(eventPublisher).publishEvent(eventCaptor.capture());
        assertThat(eventCaptor.getValue().member()).isEqualTo(member);
        assertThat(eventCaptor.getValue().order()).isEqualTo(saved);
        assertThat(eventCaptor.getValue().option()).isEqualTo(option);
    }

    @Test
    @DisplayName("주문 생성 시 위시리스트에 주문 상품이 없으면 삭제하지 않는다")
    void createOrderWithoutWish() {
        Member member = member();
        member.chargePoint(10_000);
        Option option = option();
        OrderRequest request = new OrderRequest(1L, 2, "선물 메시지");
        Order saved = order(option, 1L, 2);
        when(optionRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(option));
        when(orderRepository.save(any(Order.class))).thenReturn(saved);
        when(wishRepository.findByMemberIdAndProductId(member.getId(), option.getProduct().getId()))
            .thenReturn(Optional.empty());

        OrderResponse response = orderService.createOrder(member, request);

        assertThat(response.optionId()).isEqualTo(1L);
        verify(wishRepository, never()).delete(any());
        verify(eventPublisher).publishEvent(any(OrderCreatedEvent.class));
    }

    @Test
    @DisplayName("존재하지 않는 옵션으로 주문하면 주문 옵션 미존재 예외가 발생한다")
    void createOrderOptionNotFound() {
        Member member = member();
        OrderRequest request = new OrderRequest(999999L, 1, "선물 메시지");
        when(optionRepository.findByIdForUpdate(999999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.createOrder(member, request))
            .isInstanceOf(OrderOptionNotFoundException.class)
            .hasMessageContaining("주문할 옵션을 찾을 수 없습니다.");

        verify(optionRepository, never()).save(any());
        verify(memberRepository, never()).save(any());
        verify(orderRepository, never()).save(any());
        verify(wishRepository, never()).findByMemberIdAndProductId(any(), any());
        verify(wishRepository, never()).delete(any());
        verifyNoInteractions(eventPublisher);
    }

    @Test
    @DisplayName("재고 부족으로 주문에 실패하면 위시리스트를 정리하지 않는다")
    void createOrderInsufficientQuantityDoesNotCleanupWish() {
        Member member = member();
        member.chargePoint(10_000);
        Option option = option();
        OrderRequest request = new OrderRequest(1L, 11, "선물 메시지");
        when(optionRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(option));

        assertThatThrownBy(() -> orderService.createOrder(member, request))
            .isInstanceOf(OptionQuantityException.class);

        verify(wishRepository, never()).findByMemberIdAndProductId(any(), any());
        verify(wishRepository, never()).delete(any());
        verify(orderRepository, never()).save(any());
        verifyNoInteractions(eventPublisher);
    }

    @Test
    @DisplayName("포인트 부족으로 주문에 실패하면 위시리스트를 정리하지 않는다")
    void createOrderInsufficientPointDoesNotCleanupWish() {
        Member member = member();
        member.chargePoint(1_000);
        Option option = option();
        OrderRequest request = new OrderRequest(1L, 2, "선물 메시지");
        when(optionRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(option));

        assertThatThrownBy(() -> orderService.createOrder(member, request))
            .isInstanceOf(InsufficientMemberPointException.class);

        verify(wishRepository, never()).findByMemberIdAndProductId(any(), any());
        verify(wishRepository, never()).delete(any());
        verify(orderRepository, never()).save(any());
        verifyNoInteractions(eventPublisher);
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
