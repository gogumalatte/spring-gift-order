package gift.repository;

import gift.entity.Item;
import gift.entity.Member;
import gift.entity.Wish;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WishRepository extends JpaRepository<Wish, Long> {

    @EntityGraph(attributePaths = {"product"})
    Slice<Wish> findAllByMember(Member member, Pageable pageable);

    Optional<Wish> findByMemberAndProduct(Member member, Item product);
}