package gift.dto;

import gift.entity.Order;
import java.time.ZonedDateTime;

public record OrderResponse(
    Long id,
    Long optionId,
    int quantity,
    ZonedDateTime orderDateTime,
    String message
) {
    public static OrderResponse from(Order order) {
        return new OrderResponse(
            order.getId(),
            order.getOption().getId(),
            order.getQuantity(),
            order.getOrderDateTime(),
            order.getMessage()
        );
    }
}