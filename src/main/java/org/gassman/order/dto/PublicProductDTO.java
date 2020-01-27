package org.gassman.order.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class PublicProductDTO {
    private Long productId;
    private String name;
    private String description;
    private String unitOfMeasure;
    private BigDecimal pricePerUnit;
    private Integer availableQuantity;
    private LocalDateTime deliveryDateTime;
    private Boolean active = Boolean.TRUE;
    private Integer bookedQuantity;
}
