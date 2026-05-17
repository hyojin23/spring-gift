package gift.product;

import gift.category.Category;
import gift.category.CategoryRepository;
import gift.product.exception.AdminProductCategoryNotFoundException;
import gift.product.exception.AdminProductNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public AdminProductService(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
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

    public void createProduct(String name, int price, String imageUrl, Long categoryId) {
        Category category = getCategory(categoryId);
        productRepository.save(new Product(name, price, imageUrl, category));
    }

    public void updateProduct(Long id, String name, int price, String imageUrl, Long categoryId) {
        Product product = getProduct(id);
        Category category = getCategory(categoryId);

        product.update(name, price, imageUrl, category);
        productRepository.save(product);
    }

    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    private Category getCategory(Long id) {
        return categoryRepository.findById(id)
            .orElseThrow(() -> new AdminProductCategoryNotFoundException(id));
    }
}
