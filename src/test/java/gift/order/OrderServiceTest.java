package gift.order;

import gift.category.Category;
import gift.member.Member;
import gift.member.MemberService;
import gift.member.exception.InsufficientMemberPointException;
import gift.option.Option;
import gift.option.OptionService;
import gift.option.exception.OptionNotFoundException;
import gift.option.exception.OptionQuantityException;
import gift.order.exception.OrderOptionNotFoundException;
import gift.product.Product;
import gift.wish.WishService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class OrderServiceTest {

    private final OrderRepository orderRepository = mock(OrderRepository.class);
    private final OptionService optionService = mock(OptionService.class);
    private final MemberService memberService = mock(MemberService.class);
    private final WishService wishService = mock(WishService.class);
    private final ApplicationEventPublisher eventPublisher = mock(ApplicationEventPublisher.class);
    private final OrderService orderService = new OrderService(
        orderRepository,
        optionService,
        memberService,
        wishService,
        eventPublisher
    );

    @Test
    @DisplayName("회원의 주문 목록을 반환한다")
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
    @DisplayName("도메인 서비스 경계를 통해 주문을 생성한다")
    void createOrder() {
        Member member = member();
        member.chargePoint(10_000);
        member.updateKakaoAccessToken("kakao-token");
        Option option = option();
        OrderRequest request = new OrderRequest(1L, 2, "gift message");
        Order saved = order(option, 1L, 2);
        when(optionService.decreaseQuantityForOrder(1L, 2)).thenReturn(option);
        when(orderRepository.save(any(Order.class))).thenReturn(saved);

        OrderResponse response = orderService.createOrder(member, request);

        assertThat(response.optionId()).isEqualTo(1L);
        verify(optionService).decreaseQuantityForOrder(1L, 2);
        verify(memberService).deductPointForOrder(member, 2_000);
        verify(wishService).removeWishByProduct(member.getId(), option.getProduct().getId());
        ArgumentCaptor<OrderCreatedEvent> eventCaptor = ArgumentCaptor.forClass(OrderCreatedEvent.class);
        verify(eventPublisher).publishEvent(eventCaptor.capture());
        OrderCreatedEvent event = eventCaptor.getValue();
        assertThat(event.orderId()).isEqualTo(saved.getId());
        assertThat(event.notification().kakaoAccessToken()).isEqualTo(member.getKakaoAccessToken());
        assertThat(event.notification().productName()).isEqualTo("product");
        assertThat(event.notification().optionName()).isEqualTo("option");
        assertThat(event.notification().productPrice()).isEqualTo(1_000);
        assertThat(event.notification().quantity()).isEqualTo(2);
        assertThat(event.notification().message()).isEqualTo("gift message");
    }

    @Test
    @DisplayName("옵션 미존재 예외를 주문 옵션 미존재 예외로 변환한다")
    void createOrderOptionNotFound() {
        Member member = member();
        OrderRequest request = new OrderRequest(999999L, 1, "gift message");
        when(optionService.decreaseQuantityForOrder(999999L, 1)).thenThrow(new OptionNotFoundException());

        assertThatThrownBy(() -> orderService.createOrder(member, request))
            .isInstanceOf(OrderOptionNotFoundException.class);

        verifyNoInteractions(memberService);
        verifyNoInteractions(orderRepository);
        verifyNoInteractions(wishService);
        verifyNoInteractions(eventPublisher);
    }

    @Test
    @DisplayName("옵션 재고가 부족하면 주문을 생성하지 않는다")
    void createOrderInsufficientQuantityDoesNotCleanupWish() {
        Member member = member();
        OrderRequest request = new OrderRequest(1L, 11, "gift message");
        when(optionService.decreaseQuantityForOrder(1L, 11)).thenThrow(new OptionQuantityException("quantity"));

        assertThatThrownBy(() -> orderService.createOrder(member, request))
            .isInstanceOf(OptionQuantityException.class);

        verifyNoInteractions(memberService);
        verifyNoInteractions(orderRepository);
        verifyNoInteractions(wishService);
        verifyNoInteractions(eventPublisher);
    }

    @Test
    @DisplayName("회원 포인트가 부족하면 주문을 생성하지 않는다")
    void createOrderInsufficientPointDoesNotCleanupWish() {
        Member member = member();
        Option option = option();
        OrderRequest request = new OrderRequest(1L, 2, "gift message");
        when(optionService.decreaseQuantityForOrder(1L, 2)).thenReturn(option);
        doThrow(new InsufficientMemberPointException())
            .when(memberService)
            .deductPointForOrder(member, 2_000);

        assertThatThrownBy(() -> orderService.createOrder(member, request))
            .isInstanceOf(InsufficientMemberPointException.class);

        verifyNoInteractions(orderRepository);
        verifyNoInteractions(wishService);
        verifyNoInteractions(eventPublisher);
    }

    private Member member() {
        Member member = new Member("member@example.com", "password");
        ReflectionTestUtils.setField(member, "id", 1L);
        return member;
    }

    private Option option() {
        Product product = new Product("product", 1000, "https://example.com/product.jpg", category());
        ReflectionTestUtils.setField(product, "id", 1L);
        Option option = new Option(product, "option", 10);
        ReflectionTestUtils.setField(option, "id", 1L);
        return option;
    }

    private Order order(Option option, Long memberId, int quantity) {
        Order order = new Order(option, memberId, quantity, "gift message");
        ReflectionTestUtils.setField(order, "id", 1L);
        return order;
    }

    private Category category() {
        return new Category("category", "#FFFFFF", "https://example.com/category.jpg", "description");
    }
}
