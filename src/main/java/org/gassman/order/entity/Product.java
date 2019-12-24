package org.gassman.order.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
@Table(name = "gassman_product")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long productId;
    @Column
    private String name;
    @Column
    private String description;
    @Column
    private String unitOfMeasure;
    @Column
    private BigDecimal pricePerUnit;
    @Column
    private Integer availableQuantity;
    @Column
    private LocalDateTime deliveryDateTime;
    @Column
    private Boolean active = Boolean.TRUE;
}
