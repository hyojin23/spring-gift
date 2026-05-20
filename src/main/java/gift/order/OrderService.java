package gift.order;

import gift.member.Member;
import gift.member.MemberRepository;
import gift.option.Option;
import gift.option.OptionRepository;
import gift.order.exception.OrderOptionNotFoundException;
import gift.wish.WishRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OptionRepository optionRepository;
    private final MemberRepository memberRepository;
    private final WishRepository wishRepository;
    private final OrderNotificationService orderNotificationService;

    public OrderService(
        OrderRepository orderRepository,
        OptionRepository optionRepository,
        MemberRepository memberRepository,
        WishRepository wishRepository,
        OrderNotificationService orderNotificationService
    ) {
        this.orderRepository = orderRepository;
        this.optionRepository = optionRepository;
        this.memberRepository = memberRepository;
        this.wishRepository = wishRepository;
        this.orderNotificationService = orderNotificationService;
    }

    @Transactional(readOnly = true)
    public Page<OrderResponse> getOrders(Long memberId, Pageable pageable) {
        return orderRepository.findByMemberId(memberId, pageable)
            .map(OrderResponse::from);
    }

    @Transactional
    public OrderResponse createOrder(Member member, OrderRequest request) {
        Option option = optionRepository.findById(request.optionId())
            .orElseThrow(() -> new OrderOptionNotFoundException(request.optionId()));
        option.subtractQuantity(request.quantity());
        optionRepository.save(option);

        int totalPrice = calculateTotalPrice(option, request.quantity());
        member.deductPoint(totalPrice);
        memberRepository.save(member);

        Order saved = orderRepository.save(new Order(option, member.getId(), request.quantity(), request.message()));

        cleanupWish(member.getId(), option);
        orderNotificationService.sendOrderCreatedMessage(member, saved, option);
        return OrderResponse.from(saved);
    }

    private void cleanupWish(Long memberId, Option option) {
        wishRepository.findByMemberIdAndProductId(memberId, option.getProduct().getId())
            .ifPresent(wishRepository::delete);
    }

    private int calculateTotalPrice(Option option, int quantity) {
        return option.getProduct().getPrice() * quantity;
    }
}
