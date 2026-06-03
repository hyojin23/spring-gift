package gift.product;

import gift.category.Category;
import gift.product.exception.ProductValidationException;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import java.util.List;

@Entity
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private int price;
    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    protected Product() {
    }

    public Product(String name, int price, String imageUrl, Category category) {
        this(name, price, imageUrl, category, false);
    }

    public Product(String name, int price, String imageUrl, Category category, boolean allowKakaoName) {
        validate(name, price, imageUrl, category, allowKakaoName);
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
        this.category = category;
    }

    public void update(String name, int price, String imageUrl, Category category) {
        update(name, price, imageUrl, category, false);
    }

    public void update(String name, int price, String imageUrl, Category category, boolean allowKakaoName) {
        validate(name, price, imageUrl, category, allowKakaoName);
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
        this.category = category;
    }

    private void validate(String name, int price, String imageUrl, Category category, boolean allowKakaoName) {
        validateName(name, allowKakaoName);
        if (price <= 0) {
            throw new ProductValidationException("상품 가격은 0보다 커야 합니다.");
        }
        if (imageUrl == null || imageUrl.isBlank()) {
            throw new ProductValidationException("상품 이미지 URL은 필수입니다.");
        }
        if (category == null) {
            throw new ProductValidationException("상품 카테고리는 필수입니다.");
        }
    }

    private void validateName(String name, boolean allowKakaoName) {
        List<String> errors = ProductNameValidator.validate(name, allowKakaoName);
        if (!errors.isEmpty()) {
            throw new ProductValidationException(String.join(", ", errors));
        }
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public Category getCategory() {
        return category;
    }

}
