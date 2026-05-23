package gift.product;

import gift.category.Category;
import gift.category.CategoryRepository;
import gift.product.exception.AdminProductCategoryNotFoundException;
import gift.product.exception.AdminProductNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class AdminProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductUseCaseService productUseCaseService;

    public AdminProductService(
        ProductRepository productRepository,
        CategoryRepository categoryRepository,
        ProductUseCaseService productUseCaseService
    ) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.productUseCaseService = productUseCaseService;
    }

    public List<Product> getProducts() {
        return productRepository.findAll();
    }

    public List<Category> getCategories() {
        return categoryRepository.findAll();
    }

    public Product getProduct(Long id) {
        return productRepository.findById(id)
            .orElseThrow(() -> new AdminProductNotFoundException(id));
    }

    public List<String> validateName(String name) {
        return ProductNameValidator.validate(name, true);
    }

    @Transactional
    public void createProduct(String name, int price, String imageUrl, Long categoryId) {
        productUseCaseService.createProduct(
            new ProductCommand(name, price, imageUrl, categoryId),
            () -> new AdminProductCategoryNotFoundException(categoryId)
        );
    }

    @Transactional
    public void updateProduct(Long id, String name, int price, String imageUrl, Long categoryId) {
        productUseCaseService.updateProduct(
            id,
            new ProductCommand(name, price, imageUrl, categoryId),
            () -> new AdminProductNotFoundException(id),
            () -> new AdminProductCategoryNotFoundException(categoryId)
        );
    }

    @Transactional
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

}
