package io.payflow.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductDTO {

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Description is required")
    private String description;

    @NotBlank(message = "Price is required")
    private String price;

    @NotBlank(message = "Quantity is required")
    private String quantity;

    @NotBlank(message = "Category ID is required")
    @JsonProperty("category_id")
    private Long categoryId;
}
