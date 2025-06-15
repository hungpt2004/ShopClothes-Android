package com.example.shopclothes_android;

import java.util.ArrayList;
import java.util.List;

public class CartManager {
    private static CartManager instance;
    private List<String> cartItems;

    private CartManager() {
        cartItems = new ArrayList<>();
    }

    public static synchronized CartManager getInstance() {
        if (instance == null) {
            instance = new CartManager();
        }
        return instance;
    }

    public void addToCart(String product) {
        cartItems.add(product);
    }

    public List<String> getCartItems() {
        return cartItems;
    }
}
