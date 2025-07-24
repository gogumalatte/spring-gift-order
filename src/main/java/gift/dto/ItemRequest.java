package gift.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public class ItemRequest {

    @NotBlank
    private String name;

    @NotNull
    @Min(0)
    private int price;

    @NotBlank
    private String imageUrl;

    @Valid
    @NotEmpty(message = "상품에는 최소 하나 이상의 옵션이 필요합니다.")
    private List<OptionRequest> options;

    public ItemRequest() {
    }

    public ItemRequest(String name, int price, String imageUrl, List<OptionRequest> options) {
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
        this.options = options;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getPrice() { return price; }
    public void setPrice(int price) { this.price = price; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public List<OptionRequest> getOptions() { return options; }
    public void setOptions(List<OptionRequest> options) { this.options = options; }
}