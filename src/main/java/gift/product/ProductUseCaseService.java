package gift.product;

import gift.category.Category;
import gift.category.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.Supplier;

@Service
@Transactional(readOnly = true)
public class ProductUseCaseService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductUseCaseService(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    @Transactional
    public Product createProduct(
        ProductCommand command,
        Supplier<? extends RuntimeException> categoryNotFoundExceptionSupplier
    ) {
        Category category = findCategory(command.categoryId(), categoryNotFoundExceptionSupplier);
        return productRepository.save(
            new Product(command.name(), command.price(), command.imageUrl(), category, command.allowKakaoName())
        );
    }

    @Transactional
    public Product updateProduct(
        Long id,
        ProductCommand command,
        Supplier<? extends RuntimeException> productNotFoundExceptionSupplier,
        Supplier<? extends RuntimeException> categoryNotFoundExceptionSupplier
    ) {
        Product product = findProduct(id, productNotFoundExceptionSupplier);
        Category category = findCategory(command.categoryId(), categoryNotFoundExceptionSupplier);

        product.update(command.name(), command.price(), command.imageUrl(), category, command.allowKakaoName());
        return productRepository.save(product);
    }

    private Product findProduct(Long id, Supplier<? extends RuntimeException> exceptionSupplier) {
        return productRepository.findById(id)
            .orElseThrow(exceptionSupplier);
    }

    private Category findCategory(Long id, Supplier<? extends RuntimeException> exceptionSupplier) {
        return categoryRepository.findById(id)
            .orElseThrow(exceptionSupplier);
    }
}
