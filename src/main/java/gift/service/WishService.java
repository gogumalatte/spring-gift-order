package gift.service;

import gift.dto.WishRequest;
import gift.dto.WishResponse;
import gift.entity.Item;
import gift.entity.Member;
import gift.entity.Wish;
import gift.exception.AuthorizationException;
import gift.exception.ItemNotFoundException;
import gift.repository.ItemRepository;
import gift.repository.WishRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional(readOnly = true)
public class WishService {

    private final WishRepository wishRepository;
    private final ItemRepository itemRepository;

    public WishService(WishRepository wishRepository, ItemRepository itemRepository) {
        this.wishRepository = wishRepository;
        this.itemRepository = itemRepository;
    }

    public Slice<WishResponse> getWishes(Member member, Pageable pageable) {
        Slice<Wish> wishesSlice = wishRepository.findAllByMember(member, pageable);
        return wishesSlice.map(wish -> WishResponse.from(wish, wish.getProduct()));
    }

    @Transactional
    public WishResponse addWish(WishRequest request, Member member) {
        Item item = itemRepository.findById(request.productId())
            .orElseThrow(() -> new ItemNotFoundException("추가하려는 상품을 찾을 수 없습니다."));
        wishRepository.findByMemberAndProduct(member, item)
            .ifPresent(w -> {
                throw new DataIntegrityViolationException("이미 위시리스트에 추가된 상품입니다.");
            });
        Wish newWish = new Wish(member, item, request.quantity());
        Wish savedWish = wishRepository.save(newWish);
        return WishResponse.from(savedWish, savedWish.getProduct());
    }

    @Transactional
    public void updateWishQuantity(Long wishId, int quantity, Member loginMember) {
        Wish wish = wishRepository.findById(wishId)
            .orElseThrow(() -> new ItemNotFoundException("수정할 위시 항목을 찾을 수 없습니다."));
        if (!wish.getMember().getId().equals(loginMember.getId())) {
            throw new AuthorizationException("자신의 위시리스트만 수정할 수 있습니다.");
        }
        wish.setQuantity(quantity);
    }

    @Transactional
    public void deleteWish(Long wishId, Member loginMember) {
        Wish wish = wishRepository.findById(wishId)
            .orElseThrow(() -> new ItemNotFoundException("삭제할 위시 항목을 찾을 수 없습니다."));
        if (!wish.getMember().getId().equals(loginMember.getId())) {
            throw new AuthorizationException("자신의 위시리스트만 삭제할 수 있습니다.");
        }
        wishRepository.delete(wish);
    }
}