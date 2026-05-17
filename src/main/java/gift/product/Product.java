package gift.product;

import gift.category.Category;
import gift.option.Option;
import gift.product.exception.ProductValidationException;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private int price;
    private String imageUrl;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Option> options = new ArrayList<>();

    protected Product() {
    }

    public Product(String name, int price, String imageUrl, Category category) {
        validate(name, price, imageUrl, category);
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
        this.category = category;
    }

    public void update(String name, int price, String imageUrl, Category category) {
        validate(name, price, imageUrl, category);
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
        this.category = category;
    }

    private void validate(String name, int price, String imageUrl, Category category) {
        if (name == null || name.isBlank()) {
            throw new ProductValidationException("상품 이름은 필수입니다.");
        }
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

    public List<Option> getOptions() {
        return options;
    }
}
