package gift.category;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String color;
    private String imageUrl;
    private String description;

    protected Category() {
    }

    public Category(String name, String color, String imageUrl, String description) {
        validate(name, color, imageUrl);
        this.name = name;
        this.color = color;
        this.imageUrl = imageUrl;
        this.description = description;
    }

    public void update(String name, String color, String imageUrl, String description) {
        validate(name, color, imageUrl);
        this.name = name;
        this.color = color;
        this.imageUrl = imageUrl;
        this.description = description;
    }

    private void validate(String name, String color, String imageUrl) {
        if (name == null || name.isBlank()) {
            throw new CategoryValidationException("카테고리 이름은 필수입니다.");
        }
        if (color == null || color.isBlank()) {
            throw new CategoryValidationException("카테고리 색상은 필수입니다.");
        }
        if (imageUrl == null || imageUrl.isBlank()) {
            throw new CategoryValidationException("카테고리 이미지 URL은 필수입니다.");
        }
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getDescription() {
        return description;
    }
}
