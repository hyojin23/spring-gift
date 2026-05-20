package gift.order;

import gift.option.Option;
import gift.order.exception.OrderValidationException;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "option_id")
    private Option option;
    // primitive FK
    private Long memberId;
    private int quantity;
    private String message;
    private LocalDateTime orderDateTime;

    protected Order() {
    }

    public Order(Option option, Long memberId, int quantity, String message) {
        validateOption(option);
        validateMemberId(memberId);
        validateQuantity(quantity);
        this.option = option;
        this.memberId = memberId;
        this.quantity = quantity;
        this.message = message;
        this.orderDateTime = LocalDateTime.now();
    }

    private void validateOption(Option option) {
        if (option == null) {
            throw new OrderValidationException("주문 옵션은 필수입니다.");
        }
    }

    private void validateMemberId(Long memberId) {
        if (memberId == null) {
            throw new OrderValidationException("주문 회원 ID는 필수입니다.");
        }
    }

    private void validateQuantity(int quantity) {
        if (quantity < 1) {
            throw new OrderValidationException("주문 수량은 1 이상이어야 합니다.");
        }
    }

    public Long getId() {
        return id;
    }

    public Option getOption() {
        return option;
    }

    public Long getMemberId() {
        return memberId;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getOrderDateTime() {
        return orderDateTime;
    }
}
