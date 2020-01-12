package org.gassman.order.repository;

import org.gassman.order.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByDeliveryDateTimeAfter(LocalDateTime dateTime);

    List<Product> findByDeliveryDateTimeBeforeOrderByDeliveryDateTimeDesc(LocalDateTime dateTime);

    List<Product> findByActiveTrueAndDeliveryDateTimeAfter(LocalDateTime now);
}
