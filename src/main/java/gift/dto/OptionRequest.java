package gift.dto;

import gift.entity.Item;
import gift.entity.Option;
import gift.validation.ValidOptionName;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class OptionRequest {

    @NotBlank
    @Size(max = 50)
    @ValidOptionName
    private String name;

    @NotNull
    @Min(value = 1)
    @Max(value = 100_000_000)
    private Integer quantity;

    public OptionRequest() {
    }

    public OptionRequest(String name, Integer quantity) {
        this.name = name;
        this.quantity = quantity;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public Option toEntity(Item item) {
        return new Option(name, quantity, item);
    }
}