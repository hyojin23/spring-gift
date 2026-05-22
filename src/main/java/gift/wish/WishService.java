package gift.wish;

import gift.product.Product;
import gift.product.ProductRepository;
import gift.wish.exception.UnauthorizedWishAccessException;
import gift.wish.exception.WishNotFoundException;
import gift.wish.exception.WishProductNotFoundException;
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

        Product product = productRepository.findById(productId)
            .orElseThrow(WishProductNotFoundException::new);

        var existing = wishRepository.findByMemberIdAndProductId(memberId, product.getId()).orElse(null);

        if (existing != null) {
            return WishAddResult.existing(existing);
        }

        var saved = wishRepository.save(new Wish(memberId, product));
        return WishAddResult.created(saved);
    }

    public void removeWish(Long memberId, Long wishId) {
        var wish = wishRepository.findById(wishId)
            .orElseThrow(WishNotFoundException::new);

        if (!wish.getMemberId().equals(memberId)) {
            throw new UnauthorizedWishAccessException();
        }

        wishRepository.delete(wish);
    }

}
