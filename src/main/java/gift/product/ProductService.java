package gift.product;

import gift.product.exception.ProductCategoryNotFoundException;
import gift.product.exception.ProductDeletionNotAllowedException;
import gift.product.exception.ProductNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
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

    @Transactional
    public ProductResponse createProduct(ProductRequest request) {
        Product saved = productUseCaseService.createProduct(
            request.toCommand(),
            ProductCategoryNotFoundException::new
        );
        return ProductResponse.from(saved);
    }

    @Transactional
    public ProductResponse updateProduct(Long id, ProductRequest request) {
        Product saved = productUseCaseService.updateProduct(
            id,
            request.toCommand(),
            ProductNotFoundException::new,
            ProductCategoryNotFoundException::new
        );
        return ProductResponse.from(saved);
    }

    @Transactional
    public void deleteProduct(Long id) {
        productUseCaseService.deleteProduct(
            id,
            ProductNotFoundException::new,
            ProductDeletionNotAllowedException::new
        );
    }

    private Product findProduct(Long id) {
        return productRepository.findById(id)
            .orElseThrow(ProductNotFoundException::new);
    }
}
