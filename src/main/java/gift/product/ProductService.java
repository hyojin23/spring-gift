package gift.product;

import gift.product.exception.ProductCategoryNotFoundException;
import gift.product.exception.ProductNotFoundException;
import gift.product.exception.ProductValidationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductUseCaseService productUseCaseService;

    public ProductService(
        ProductRepository productRepository,
        ProductUseCaseService productUseCaseService
    ) {
        this.productRepository = productRepository;
        this.productUseCaseService = productUseCaseService;
    }

    public Page<ProductResponse> getProducts(Pageable pageable) {
        return productRepository.findAll(pageable)
            .map(ProductResponse::from);
    }

    public ProductResponse getProduct(Long id) {
        return ProductResponse.from(findProduct(id));
    }

    public ProductResponse createProduct(ProductRequest request) {
        validateName(request.name());

        Product saved = productUseCaseService.createProduct(
            request.toCommand(),
            ProductCategoryNotFoundException::new
        );
        return ProductResponse.from(saved);
    }

    public ProductResponse updateProduct(Long id, ProductRequest request) {
        validateName(request.name());

        Product saved = productUseCaseService.updateProduct(
            id,
            request.toCommand(),
            ProductNotFoundException::new,
            ProductCategoryNotFoundException::new
        );
        return ProductResponse.from(saved);
    }

    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    private Product findProduct(Long id) {
        return productRepository.findById(id)
            .orElseThrow(ProductNotFoundException::new);
    }

    private void validateName(String name) {
        List<String> errors = ProductNameValidator.validate(name);
        if (!errors.isEmpty()) {
            throw new ProductValidationException(String.join(", ", errors));
        }
    }
}
