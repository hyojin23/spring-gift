package gift.order;

import gift.member.Member;
import gift.member.MemberRepository;
import gift.option.Option;
import gift.option.OptionRepository;
import gift.order.exception.OrderOptionNotFoundException;
import gift.wish.WishRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;
    private final OptionRepository optionRepository;
    private final MemberRepository memberRepository;
    private final WishRepository wishRepository;
    private final ApplicationEventPublisher eventPublisher;

    public OrderService(
        OrderRepository orderRepository,
        OptionRepository optionRepository,
        MemberRepository memberRepository,
        WishRepository wishRepository,
        ApplicationEventPublisher eventPublisher
    ) {
        this.orderRepository = orderRepository;
        this.optionRepository = optionRepository;
        this.memberRepository = memberRepository;
        this.wishRepository = wishRepository;
        this.eventPublisher = eventPublisher;
    }

    public Page<OrderResponse> getOrders(Long memberId, Pageable pageable) {
        return orderRepository.findByMemberId(memberId, pageable)
            .map(OrderResponse::from);
    }

    @Transactional
    public OrderResponse createOrder(Member member, OrderRequest request) {
        Option option = optionRepository.findByIdForUpdate(request.optionId())
            .orElseThrow(() -> new OrderOptionNotFoundException(request.optionId()));
        option.subtractQuantity(request.quantity());
        optionRepository.save(option);

        int totalPrice = calculateTotalPrice(option, request.quantity());
        member.deductPoint(totalPrice);
        memberRepository.save(member);

        Order saved = orderRepository.save(new Order(option, member.getId(), request.quantity(), request.message()));

        cleanupWish(member.getId(), option);
        eventPublisher.publishEvent(new OrderCreatedEvent(saved.getId(), createNotificationPayload(member, option, request)));
        return OrderResponse.from(saved);
    }

    private void cleanupWish(Long memberId, Option option) {
        wishRepository.findByMemberIdAndProductId(memberId, option.getProduct().getId())
            .ifPresent(wishRepository::delete);
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
