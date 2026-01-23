package com.example.my_spring_server.foods.domain;

public class Foods {
    /*
    음식은 고유 ID를 가진다.
    음식은 고유한 이름을 가진다.
    음식은 가격(price)을 가진다.
    음식은 재고 수량(stock)을 가진다.
    재고 수량은 0 미만이 될 수 없다.
     */
    private Long id;
    private String name;
    private int price;
    private int stock;

    public Foods(String name, int price, int stock) {
        if(name == null || price <= 0 || stock < 0)
            throw new IllegalStateException("Foods 생성 오류");

        this.name = name;
        this.price = price;
        this.stock = stock;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public int getStock() {
        return stock;
    }
}
