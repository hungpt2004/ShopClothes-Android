package com.example.shopclothes_android;
public class Product {
    private String name;
    private double price;
    private int imageResId; // drawable resource id

    public Product(String name, double price, int imageResId) {
        this.name = name;
        this.price = price;
        this.imageResId = imageResId;
    }

    public String getName() { return name; }
    public double getPrice() { return price; }
    public int getImageResId() { return imageResId; }
}

