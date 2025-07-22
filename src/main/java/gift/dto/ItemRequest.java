package gift.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import gift.validation.ValidProductName;
import java.util.List;

public record ItemRequest(

    @ValidProductName
    @NotBlank(message = "상품 이름은 비어 있을 수 없습니다.")
    String name,

    @Min(value = 0, message = "가격은 0보다 작을 수 없습니다.")
    @NotNull(message = "가격은 비어 있을 수 없습니다.")
    int price,

    @NotBlank(message = "이미지 URL은 비어 있을 수 없습니다.")
    String imageUrl,

    @Valid
    @NotEmpty(message = "상품에는 최소 하나 이상의 옵션이 필요합니다.")
    List<OptionRequest> options
) {

}