package gift.dto;

import gift.entity.Item;
import java.util.List;
import java.util.stream.Collectors;

public record ItemResponse(
    Long id,
    String name,
    int price,
    String imageUrl,
    List<OptionResponse> options
) {
    public static ItemResponse from(Item item) {
        List<OptionResponse> optionResponses = item.getOptions().stream()
            .map(OptionResponse::from)
            .collect(Collectors.toList());

        return new ItemResponse(
            item.getId(),
            item.getName(),
            item.getPrice(),
            item.getImageUrl(),
            optionResponses
        );
    }
}