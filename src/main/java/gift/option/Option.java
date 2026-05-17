package gift.option;

import gift.product.Product;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "options")
public class Option {
    private static final int MIN_QUANTITY = 1;
    private static final int MAX_QUANTITY = 99_999_999;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false)
    private int quantity;

    protected Option() {
    }

    public Option(Product product, String name, int quantity) {
        validateQuantity(quantity);
        this.product = product;
        this.name = name;
        this.quantity = quantity;
    }

    public void subtractQuantity(int amount) {
        validateSubtractAmount(amount);
        if (amount > this.quantity) {
            throw new IllegalArgumentException("차감할 수량이 현재 재고보다 많습니다.");
        }
        this.quantity -= amount;
    }

    private void validateQuantity(int quantity) {
        if (quantity < MIN_QUANTITY || quantity > MAX_QUANTITY) {
            throw new IllegalArgumentException("옵션 수량은 1 이상 99,999,999 이하이어야 합니다.");
        }
    }

    private void validateSubtractAmount(int amount) {
        if (amount < MIN_QUANTITY) {
            throw new IllegalArgumentException("차감 수량은 1 이상이어야 합니다.");
        }
    }

    public Long getId() {
        return id;
    }

    public Product getProduct() {
        return product;
    }

    public String getName() {
        return name;
    }

    public int getQuantity() {
        return quantity;
    }
}
