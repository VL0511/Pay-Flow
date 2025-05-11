package io.payflow.repository;

import io.payflow.enums.PaymentStatus;
import io.payflow.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
