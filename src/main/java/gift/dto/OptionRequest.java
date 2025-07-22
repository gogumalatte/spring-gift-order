package gift.dto;

import gift.entity.Item;
import gift.entity.Option;
import gift.validation.ValidOptionName;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record OptionRequest(
    @NotBlank
    @Size(max = 50, message = "옵션 이름은 최대 50자까지 입력할 수 있습니다.")
    @ValidOptionName
    String name,

    @NotNull
    @Min(value = 1, message = "옵션 수량은 1개 이상이어야 합니다.")
    @Max(value = 100_000_000, message = "옵션 수량은 1억 개 미만이어야 합니다.")
    int quantity
) {
    public Option toEntity(Item item) {
        return new Option(name, quantity, item);
    }
}