package gift.category;

import gift.category.exception.CategoryDeletionNotAllowedException;
import gift.product.ProductRepository;
import org.springframework.stereotype.Component;

@Component
public class CategoryDeletionPolicy {

    private final ProductRepository productRepository;

    public CategoryDeletionPolicy(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public void validateDeletable(Long categoryId) {
        if (productRepository.existsByCategoryId(categoryId)) {
            throw new CategoryDeletionNotAllowedException();
        }
    }
}
