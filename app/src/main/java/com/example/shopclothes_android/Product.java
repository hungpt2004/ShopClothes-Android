package com.example.shopclothes_android;

public class Product {
    private String name;
    private double price;
    private int imageResId; // drawable resource id
    private int quantity;
    private boolean isFavorite;

    public Product(String name, double price, int imageResId) {
        this.name = name;
        this.price = price;
        this.imageResId = imageResId;
        this.quantity = 1; // Default quantity
        this.isFavorite = false;
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
