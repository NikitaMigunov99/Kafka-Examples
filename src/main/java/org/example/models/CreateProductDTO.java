package org.example.models;

import java.math.BigDecimal;

public record CreateProductDTO(
        String title,
        BigDecimal price,
        Integer quantity
) {
}
