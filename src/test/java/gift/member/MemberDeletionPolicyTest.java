package gift.member;

import gift.member.exception.AdminMemberDeletionNotAllowedException;
import gift.order.OrderRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MemberDeletionPolicyTest {

    private final OrderRepository orderRepository = mock(OrderRepository.class);
    private final MemberDeletionPolicy memberDeletionPolicy = new MemberDeletionPolicy(orderRepository);

    @Test
    @DisplayName("주문 이력이 있으면 회원을 삭제할 수 없다")
    void validateDeletableWithOrders() {
        when(orderRepository.existsByMemberId(1L)).thenReturn(true);

        assertThatThrownBy(() -> memberDeletionPolicy.validateDeletable(1L))
            .isInstanceOf(AdminMemberDeletionNotAllowedException.class);
    }

    @Test
    @DisplayName("회원 삭제 가능 여부를 주문 이력으로 확인한다")
    void validateDeletable() {
        when(orderRepository.existsByMemberId(1L)).thenReturn(false);

        memberDeletionPolicy.validateDeletable(1L);

        verify(orderRepository).existsByMemberId(1L);
    }
}
