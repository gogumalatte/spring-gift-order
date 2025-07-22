package gift.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record WishRequest(
    @NotNull
    Long productId,

    @NotNull
    @Min(value = 1, message = "수량은 1 이상이어야 합니다.")
    int quantity
) {
}