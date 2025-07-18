package org.example.models.event;

public class UpdateProductEvent {

    private String productId;
    private String title;

    public UpdateProductEvent() {}

    public UpdateProductEvent(String productId, String title) {
        this.productId = productId;
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    @Override
    public String toString() {
        return "UpdateProductEvent{" +
                "productId='" + productId + '\'' +
                ", title='" + title + '\'' +
                '}';
    }
}
