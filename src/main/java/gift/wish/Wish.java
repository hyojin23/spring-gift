package gift.wish;

import gift.product.Product;
import gift.wish.exception.WishValidationException;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Wish {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    // primitive FK - no entity reference
    private Long memberId;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    protected Wish() {
    }

    public Wish(Long memberId, Product product) {
        validate(memberId, product);
        this.memberId = memberId;
        this.product = product;
    }

    private void validate(Long memberId, Product product) {
        if (memberId == null) {
            throw new WishValidationException("회원 id는 필수입니다.");
        }
        if (product == null) {
            throw new WishValidationException("위시 상품은 필수입니다.");
        }
    }

    public Long getId() {
        return id;
    }

    public Long getMemberId() {
        return memberId;
    }

    public Product getProduct() {
        return product;
    }
}
