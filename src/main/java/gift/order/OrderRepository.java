package gift.order;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Page<Order> findByMemberId(Long memberId, Pageable pageable);

    boolean existsByMemberId(Long memberId);

    @Query("select count(o) > 0 from Order o where o.option.id = :optionId")
    boolean existsByOptionId(@Param("optionId") Long optionId);
}
