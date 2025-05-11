package io.payflow.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderItemDTO {
    private Long productId;
    private Integer quantity;
    private Double price;
}
