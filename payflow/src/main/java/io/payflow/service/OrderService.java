package io.payflow.service;

import io.payflow.component.JwtTokenProvider;
import io.payflow.dto.OrderDTO;
import io.payflow.dto.OrderItemResponseDTO;
import io.payflow.dto.OrderResponseDTO;
import io.payflow.enums.PaymentStatus;
import io.payflow.exception.AuthenticationException;
import io.payflow.exception.ResourceNotFoundException;
import io.payflow.model.Order;
import io.payflow.model.OrderItem;
import io.payflow.model.User;
import io.payflow.repository.OrderItemRepository;
import io.payflow.repository.OrderRepository;
import io.payflow.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    public List<OrderResponseDTO> findAllFormatted() {
        List<Order> orders = orderRepository.findAll();
        return orders.stream().map(order -> OrderResponseDTO.builder()
                .id(order.getId())
                .paypalLink(order.getPaypalLink())
                .totalAmount(order.getTotalAmount())
                .orderDate(order.getOrderDate())
                .paymentStatus(order.getPaymentStatus().name())
                .items(order.getItems().stream().map(item -> OrderItemResponseDTO.builder()
                        .productName(item.getProduct().getName())
                        .quantity(item.getQuantity())
                        .price(item.getPrice())
                        .build()).toList())
                .build()
        ).toList();
    }

    public Order findById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
    }

    public Order save(OrderDTO orderDTO, String token) {
        User user = getUserFromToken(token);

        double totalAmount = 0.0;
        for (OrderItem item : orderDTO.getItems()) {
            double itemTotal = item.getPrice() * item.getQuantity();
            totalAmount += itemTotal;
        }

        Order order = Order.builder()
                .paypalLink(orderDTO.getPaypalLink())
                .paymentStatus(orderDTO.getPaymentStatus())
                .orderDate(orderDTO.getOrderDate())
                .totalAmount(totalAmount)
                .user(user)
                .build();

        Order savedOrder = orderRepository.save(order);

        for (OrderItem item : orderDTO.getItems()) {
            item.setOrder(savedOrder);
            orderItemRepository.save(item);
        }

        return savedOrder;
    }

    public Order update(Long id, OrderDTO orderDTO) {
        Order existing = findById(id);
        updateOrderFields(existing, orderDTO);
        return orderRepository.save(existing);
    }

    public void deleteOrder(Long id) {
        Order existing = findById(id);
        orderRepository.delete(existing);
    }

    public Order getOrderOrThrow(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
    }

    public void updateOrderFields(Order existing, OrderDTO orderDTO) {
        existing.setTotalAmount(orderDTO.getTotalAmount());
        existing.setPaymentStatus(orderDTO.getPaymentStatus());
        existing.setOrderDate(orderDTO.getOrderDate());
    }

    public Order convertToEntity(OrderDTO orderDTO, User user) {
        return Order.builder()
                .totalAmount(orderDTO.getTotalAmount())
                .paymentStatus(orderDTO.getPaymentStatus())
                .orderDate(orderDTO.getOrderDate())
                .user(user)
                .build();
    }


    private User getUserFromToken(String token) {
        try {
            String email = jwtTokenProvider.getUsernameFromToken(token.replace("Bearer ", ""));
            return userRepository.findByEmail(email)
                    .orElseThrow(() -> new AuthenticationException("Credenciais inválidas ou usuário não encontrado"));
        } catch (Exception e) {
            throw new AuthenticationException("Token inválido ou expirado");
        }
    }

}
