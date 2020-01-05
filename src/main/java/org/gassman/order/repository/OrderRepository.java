package org.gassman.order.repository;

import org.gassman.order.entity.Order;
import org.gassman.order.entity.Product;
import org.gassman.order.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserAndAndProduct_DeliveryDateTimeAfter(User user, LocalDateTime now);
    List<Order> findByProduct(Product product);
    List<Order> findAllByPaidFalse();
    List<Order> findByProduct_DeliveryDateTimeAfter(LocalDateTime now);
}
