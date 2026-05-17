package gift.option;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OptionRepository extends JpaRepository<Option, Long> {
    List<Option> findByProductId(Long productId);

    boolean existsByProductIdAndName(Long productId, String name);

    long countByProductId(Long productId);

    Optional<Option> findByIdAndProductId(Long optionId, Long productId);
}
