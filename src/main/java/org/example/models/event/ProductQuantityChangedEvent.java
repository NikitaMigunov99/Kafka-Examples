package org.example.models.event;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public class ProductQuantityChangedEvent {

    private String productId;
    private int quantity;

    public ProductQuantityChangedEvent() {}

    public ProductQuantityChangedEvent(String productId, int quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }

}
