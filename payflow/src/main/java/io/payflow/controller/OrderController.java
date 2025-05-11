package io.payflow.controller;

import io.payflow.component.JwtTokenProvider;
import io.payflow.dto.OrderDTO;
import io.payflow.dto.OrderResponseDTO;
import io.payflow.enums.PaymentStatus;
import io.payflow.exception.InternalServerErrorException;
import io.payflow.exception.MethodNotAllowedException;
import io.payflow.exception.ResourceNotFoundException;
import io.payflow.model.Order;
import io.payflow.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final JwtTokenProvider jwtTokenProvider;


    @GetMapping
    public ResponseEntity<List<OrderResponseDTO>> findAllFormatted() {
        List<OrderResponseDTO> orders = orderService.findAllFormatted();
        if (orders.isEmpty()) {
            throw new ResourceNotFoundException("Order");  // Or another condition based on your logic
        }
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> findById(@PathVariable Long id, HttpServletRequest request) {
        if (!HttpMethod.GET.matches(request.getMethod())) {
            throw new MethodNotAllowedException("GET method is not allowed here.");
        }

        Order order = orderService.findById(id);
        return ResponseEntity.ok(order);
    }

    @PostMapping
    public ResponseEntity<Order> create(@RequestBody OrderDTO orderDTO, @RequestHeader("Authorization") String token) {
        try {
            orderDTO.setOrderDate(LocalDateTime.now());
            orderDTO.setPaymentStatus(PaymentStatus.PENDING);
            Order createdOrder = orderService.save(orderDTO, token);
            return ResponseEntity.status(201).body(createdOrder);
        } catch (Exception e) {
            throw new InternalServerErrorException("An unexpected error occurred while creating the order.");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Order> update(@PathVariable Long id, @RequestBody OrderDTO order) {
        Order updatedOrder = orderService.update(id, order);

        if (updatedOrder == null) {
            throw new ResourceNotFoundException("Order " + id);
        }

        return ResponseEntity.ok(updatedOrder);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }

}
