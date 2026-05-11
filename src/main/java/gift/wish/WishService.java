package gift.wish;

import gift.product.Product;
import gift.product.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class WishService {

    private final WishRepository wishRepository;
    private final ProductRepository productRepository;

    public WishService(WishRepository wishRepository, ProductRepository productRepository) {
        this.wishRepository = wishRepository;
        this.productRepository = productRepository;
    }

    public Page<WishResponse> getWishes(Long memberId, Pageable pageable) {
        return wishRepository.findByMemberId(memberId, pageable)
                .map(WishResponse::from);
    }

    public WishAddResult addWish(Long memberId, Long productId) {

        Product product = productRepository.findById(productId).orElse(null);

        if (product == null) {
            return null;
        }

        var existing = wishRepository.findByMemberIdAndProductId(memberId, product.getId()).orElse(null);

        if (existing != null) {
            return WishAddResult.existing(existing);
        }

        var saved = wishRepository.save(new Wish(memberId, product));
        return WishAddResult.created(saved);
    }

}
