package gift.wish;

import gift.product.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class WishService {

    private final WishRepository wishRepository;

    public WishService(WishRepository wishRepository) {
        this.wishRepository = wishRepository;
    }

    public Page<WishResponse> getWishes(Long memberId, Pageable pageable) {
        return wishRepository.findByMemberId(memberId, pageable)
                .map(WishResponse::from);
    }

    public WishResponse addWish(Long memberId, Product product) {

        var saved = wishRepository.save(new Wish(memberId, product));
        return WishResponse.from(saved);
    }

}
