package io.payflow.dto;

import io.payflow.enums.PaymentStatus;
import io.payflow.model.OrderItem;
import io.payflow.model.User;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Data
@Builder
public class OrderDTO {
    private String paypalLink;
    private Double totalAmount;
    private PaymentStatus paymentStatus;
    private LocalDateTime orderDate;
    private Long userId;
    private List<OrderItem> items;
}
