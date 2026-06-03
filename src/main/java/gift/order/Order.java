package gift.order;

import gift.option.Option;
import gift.order.exception.OrderErrorCode;
import gift.order.exception.OrderException;
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
            throw new OrderException(OrderErrorCode.OPTION_REQUIRED);
        }
    }

    private void validateMemberId(Long memberId) {
        if (memberId == null) {
            throw new OrderException(OrderErrorCode.MEMBER_ID_REQUIRED);
        }
    }

    private void validateQuantity(int quantity) {
        if (quantity < 1) {
            throw new OrderException(OrderErrorCode.INVALID_QUANTITY);
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
