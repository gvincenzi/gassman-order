package org.gassman.order.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
@Table(name = "gassman_order")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long orderId;
    @Column
    private Integer quantity;
    @Column(nullable = false)
    private Boolean payed = Boolean.FALSE;
    @Column
    private String paymentExternalReference;
    @Column
    private LocalDateTime paymentExternalDateTime;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="productId", nullable = false)
    private Product product;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="id", nullable = false)
    private User user;
}
