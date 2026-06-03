package gift.order;

import gift.member.Member;
import gift.member.MemberService;
import gift.option.Option;
import gift.option.OptionService;
import gift.option.exception.OptionNotFoundException;
import gift.order.exception.OrderErrorCode;
import gift.order.exception.OrderException;
import gift.wish.WishService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;
    private final OptionService optionService;
    private final MemberService memberService;
    private final WishService wishService;
    private final ApplicationEventPublisher eventPublisher;

    public OrderService(
        OrderRepository orderRepository,
        OptionService optionService,
        MemberService memberService,
        WishService wishService,
        ApplicationEventPublisher eventPublisher
    ) {
        this.orderRepository = orderRepository;
        this.optionService = optionService;
        this.memberService = memberService;
        this.wishService = wishService;
        this.eventPublisher = eventPublisher;
    }

    public Page<OrderResponse> getOrders(Long memberId, Pageable pageable) {
        return orderRepository.findByMemberId(memberId, pageable)
            .map(OrderResponse::from);
    }

    @Transactional
    public OrderResponse createOrder(Member member, OrderRequest request) {
        Option option = decreaseOptionQuantity(request);
        int totalPrice = calculateTotalPrice(option, request.quantity());
        memberService.deductPointForOrder(member, totalPrice);

        Order saved = orderRepository.save(new Order(option, member.getId(), request.quantity(), request.message()));

        wishService.removeWishByProduct(member.getId(), option.getProduct().getId());
        eventPublisher.publishEvent(new OrderCreatedEvent(saved.getId(), createNotificationPayload(member, option, request)));
        return OrderResponse.from(saved);
    }

    private Option decreaseOptionQuantity(OrderRequest request) {
        try {
            return optionService.decreaseQuantityForOrder(request.optionId(), request.quantity());
        } catch (OptionNotFoundException exception) {
            throw new OrderException(OrderErrorCode.OPTION_NOT_FOUND, request.optionId());
        }
    }

    private int calculateTotalPrice(Option option, int quantity) {
        return option.getProduct().getPrice() * quantity;
    }

    private OrderNotificationPayload createNotificationPayload(Member member, Option option, OrderRequest request) {
        return new OrderNotificationPayload(
            member.getKakaoAccessToken(),
            option.getProduct().getName(),
            option.getName(),
            option.getProduct().getPrice(),
            request.quantity(),
            request.message()
        );
    }
}
