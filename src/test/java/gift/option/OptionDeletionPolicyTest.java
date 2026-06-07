package gift.option;

import gift.option.exception.OrderedOptionDeletionNotAllowedException;
import gift.order.OrderRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class OptionDeletionPolicyTest {

    private final OrderRepository orderRepository = mock(OrderRepository.class);
    private final OptionDeletionPolicy optionDeletionPolicy = new OptionDeletionPolicy(orderRepository);

    @Test
    @DisplayName("주문 이력이 있으면 옵션을 삭제할 수 없다")
    void validateDeletableWithOrders() {
        when(orderRepository.existsByOptionId(1L)).thenReturn(true);

        assertThatThrownBy(() -> optionDeletionPolicy.validateDeletable(1L))
            .isInstanceOf(OrderedOptionDeletionNotAllowedException.class);
    }

    @Test
    @DisplayName("옵션 삭제 가능 여부를 주문 이력으로 확인한다")
    void validateDeletable() {
        when(orderRepository.existsByOptionId(1L)).thenReturn(false);

        optionDeletionPolicy.validateDeletable(1L);

        verify(orderRepository).existsByOptionId(1L);
    }
}
