package com.example.shopclothes_android;

import java.util.ArrayList;
import java.util.List;

public class CartManager {
    private static CartManager instance;
    private List<Product> cartItems;
    private static final double SHIPPING_FEE = 5.00;

    private CartManager() {
        cartItems = new ArrayList<>();
    }

    public static synchronized CartManager getInstance() {
        if (instance == null) {
            instance = new CartManager();
        }
        return instance;
    }

    public void addToCart(Product product) {
        // Check if product already exists in cart
        for (Product item : cartItems) {
            if (item.getName().equals(product.getName())) {
                item.setQuantity(item.getQuantity() + 1);
                return;
            }
        }
        // If not found, add new product
        cartItems.add(product);
    }

    public List<Product> getCartItems() {
        return cartItems;
    }

    public double getSubtotal() {
        double subtotal = 0;
        for (Product item : cartItems) {
            subtotal += item.getTotalPrice();
        }
        return subtotal;
    }

    public double getShippingFee() {
        return SHIPPING_FEE;
    }

    public double getTotal() {
        return getSubtotal() + getShippingFee();
    }

    public void clearCart() {
        cartItems.clear();
    }
}
