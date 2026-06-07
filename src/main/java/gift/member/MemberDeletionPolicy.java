package gift.member;

import gift.member.exception.AdminMemberDeletionNotAllowedException;
import gift.order.OrderRepository;
import org.springframework.stereotype.Component;

@Component
public class MemberDeletionPolicy {

    private final OrderRepository orderRepository;

    public MemberDeletionPolicy(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public void validateDeletable(Long memberId) {
        if (orderRepository.existsByMemberId(memberId)) {
            throw new AdminMemberDeletionNotAllowedException(memberId);
        }
    }
}
