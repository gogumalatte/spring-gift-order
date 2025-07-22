package gift.controller.api;

import gift.dto.WishRequest;
import gift.dto.WishResponse;
import gift.dto.WishUpdateRequest;
import gift.entity.Member;
import gift.login.Authenticated;
import gift.login.Login;
import gift.service.WishService;
import jakarta.validation.Valid;
import java.net.URI;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/wishes")
@Authenticated
public class WishController {

    private final WishService wishService;

    public WishController(WishService wishService) {
        this.wishService = wishService;
    }

    @GetMapping
    public ResponseEntity<Slice<WishResponse>> getWishes(
        @Login Member member,
        @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        Slice<WishResponse> wishes = wishService.getWishes(member, pageable);
        return ResponseEntity.ok(wishes);
    }

    @PostMapping
    public ResponseEntity<WishResponse> addWish(@Valid @RequestBody WishRequest request, @Login Member member) {
        WishResponse newWish = wishService.addWish(request, member);
        URI location = URI.create("/api/wishes/" + newWish.wishId());
        return ResponseEntity.created(location).body(newWish);
    }

    @PatchMapping("/{wishId}")
    public ResponseEntity<Void> updateWishQuantity(
        @PathVariable("wishId") Long wishId,
        @Valid @RequestBody WishUpdateRequest request,
        @Login Member loginMember
    ) {
        wishService.updateWishQuantity(wishId, request.quantity(), loginMember);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{wishId}")
    public ResponseEntity<Void> deleteWish(
        @PathVariable("wishId") Long wishId,
        @Login Member loginMember
    ) {
        wishService.deleteWish(wishId, loginMember);
        return ResponseEntity.noContent().build();
    }
}