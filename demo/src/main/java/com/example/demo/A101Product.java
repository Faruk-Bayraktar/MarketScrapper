package com.example.demo;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "a101") // MOngoDb ye kaydedilecek collection adı
public class A101Product {

    private String id; // MongoDb deki id alanı
    private String name;
    private String price;
    private boolean discount;

    public A101Product(String id, String name, String price, boolean discount) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.discount = discount;
    }
//Getter Ve Setterlar

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public boolean isDiscount() {
        return discount;
    }

    public void setDiscount(boolean discount) {
        this.discount = discount;
    }
}
