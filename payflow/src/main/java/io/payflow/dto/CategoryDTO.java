package io.payflow.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CategoryDTO {

    @NotBlank(message = "Name is required")
    private String name;
}
