package com.example.shopclothes_android;

import java.io.Serializable;

public class Product implements Serializable {
    private int id; // for SQLite
    private String name;
    private double price;
    private int imageResId; // drawable resource id
    private String imageUri; // for custom images
    private int quantity;
    private boolean isFavorite;

    public Product(String name, double price, int imageResId) {
        this.name = name;
        this.price = price;
        this.imageResId = imageResId;
        this.imageUri = null;
        this.quantity = 1; // Default quantity
        this.isFavorite = false;
    }

    public String getImageUri() {
        return imageUri;
    }
    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public int getImageResId() {
        return imageResId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getTotalPrice() {
        return price * quantity;
    }

    public boolean isFavorite() {
        return FavoriteManager.getInstance().isFavorite(this);
    }

    public void toggleFavorite() {
        FavoriteManager.getInstance().toggleFavorite(this);
    }

    @Override
    public String toString() {
        return name + " - $" + String.format("%.2f", price) + " x " + quantity;
    }
}
