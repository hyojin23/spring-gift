package gift.option;

import gift.option.exception.DuplicateOptionNameException;
import gift.option.exception.OptionDeletionNotAllowedException;
import gift.option.exception.OptionNotFoundException;
import gift.option.exception.OptionProductNotFoundException;
import gift.product.Product;
import gift.product.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class OptionService {

    private final OptionRepository optionRepository;
    private final ProductRepository productRepository;

    public OptionService(OptionRepository optionRepository, ProductRepository productRepository) {
        this.optionRepository = optionRepository;
        this.productRepository = productRepository;
    }

    public List<OptionResponse> getOptions(Long productId) {
        findProduct(productId);

        return optionRepository.findByProductId(productId).stream()
            .map(OptionResponse::from)
            .toList();
    }

    @Transactional
    public OptionResponse createOption(Long productId, OptionRequest request) {
        Product product = findProduct(productId);
        validateDuplicateName(productId, request.name());

        Option saved = optionRepository.save(new Option(product, request.name(), request.quantity()));
        return OptionResponse.from(saved);
    }

    @Transactional
    public void deleteOption(Long productId, Long optionId) {
        findProduct(productId);
        validateCanDelete(productId);

        Option option = optionRepository.findByIdAndProductId(optionId, productId)
            .orElseThrow(OptionNotFoundException::new);

        optionRepository.delete(option);
    }

    private Product findProduct(Long productId) {
        return productRepository.findById(productId)
            .orElseThrow(OptionProductNotFoundException::new);
    }

    private void validateDuplicateName(Long productId, String name) {
        if (optionRepository.existsByProductIdAndName(productId, name)) {
            throw new DuplicateOptionNameException();
        }
    }

    private void validateCanDelete(Long productId) {
        if (optionRepository.countByProductId(productId) <= 1) {
            throw new OptionDeletionNotAllowedException();
        }
    }
}
