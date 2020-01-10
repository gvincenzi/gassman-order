package org.gassman.order.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return productId.equals(product.productId) &&
                name.equals(product.name) &&
                description.equals(product.description) &&
                unitOfMeasure.equals(product.unitOfMeasure) &&
                pricePerUnit.compareTo(product.pricePerUnit) == 0 &&
                availableQuantity.equals(product.availableQuantity) &&
                deliveryDateTime.equals(product.deliveryDateTime) &&
                active.equals(product.active);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId, name, description, unitOfMeasure, pricePerUnit, availableQuantity, deliveryDateTime, active);
    }
}
