package com.example.my_spring_server.orders.domain;

public class OrderItems {
    private Long id;
    private long orderId;
    private long foodId;
    private int priceAtOrder;
    private int quantity;

    protected OrderItems() { }

    protected OrderItems (long foodId, int priceAtOrder, int quantity) {
        this.foodId = foodId;
        this.priceAtOrder = priceAtOrder;
        this.quantity = quantity;
    }

    public long getOrderId() {
        return orderId;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getPriceAtOrder() {
        return priceAtOrder;
    }

    public long getFoodId() {
        return foodId;
    }

    public Long getId() {
        return id;
    }

    public int getTotalPrice() {
        return priceAtOrder * quantity;
    }
}
