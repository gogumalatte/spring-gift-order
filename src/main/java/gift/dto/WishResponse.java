package gift.dto;

import gift.entity.Item;
import gift.entity.Wish;

public record WishResponse(
    Long wishId,
    int quantity,
    ItemResponse product
) {
    public static WishResponse from(Wish wish, Item item) {
        return new WishResponse(wish.getId(), wish.getQuantity(), ItemResponse.from(item));
    }
}