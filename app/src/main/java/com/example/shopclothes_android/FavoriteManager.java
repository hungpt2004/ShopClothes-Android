package com.example.shopclothes_android;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FavoriteManager {
    private static FavoriteManager instance;
    private final Set<String> favoriteProductNames;
    private final List<Product> favoriteProducts;

    private FavoriteManager() {
        favoriteProductNames = new HashSet<>();
        favoriteProducts = new ArrayList<>();
    }

    public static synchronized FavoriteManager getInstance() {
        if (instance == null) {
            instance = new FavoriteManager();
        }
        return instance;
    }

    public void toggleFavorite(Product product) {
        String productName = product.getName();
        if (favoriteProductNames.contains(productName)) {
            favoriteProductNames.remove(productName);
            favoriteProducts.removeIf(p -> p.getName().equals(productName));
        } else {
            favoriteProductNames.add(productName);
            favoriteProducts.add(product);
        }
    }

    public boolean isFavorite(Product product) {
        return favoriteProductNames.contains(product.getName());
    }

    public List<Product> getFavoriteProducts() {
        return favoriteProducts;
    }

    public void clearFavorites() {
        favoriteProductNames.clear();
        favoriteProducts.clear();
    }
}