package io.payflow.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class OrderResponseDTO {
    private Long id;
    private String paypalLink;
    private Double totalAmount;
    private LocalDateTime orderDate;
    private String paymentStatus;
    private List<OrderItemResponseDTO> items;
}
