package gift.option;

import gift.option.exception.OrderedOptionDeletionNotAllowedException;
import gift.order.OrderRepository;
import org.springframework.stereotype.Component;

@Component
public class OptionDeletionPolicy {

    private final OrderRepository orderRepository;

    public OptionDeletionPolicy(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public void validateDeletable(Long optionId) {
        if (orderRepository.existsByOptionId(optionId)) {
            throw new OrderedOptionDeletionNotAllowedException();
        }
    }
}
