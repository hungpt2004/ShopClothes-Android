package com.example.shopclothes_android;

import android.content.Context;
import android.util.Log;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class ProductManager {
    private static final String TAG = "ProductManager";
    private static final String PRODUCT_FILE = "products.dat";
    private static ProductManager instance;
    private List<Product> products;
    private Context context;

    private ProductManager() {
        products = new ArrayList<>();
    }

    public static synchronized ProductManager getInstance() {
        if (instance == null) {
            instance = new ProductManager();
        }
        return instance;
    }

    public void initialize(Context context) {
        this.context = context;
        loadProducts();
        if (products.isEmpty()) {
            // Add default products
            products.add(new Product("T-shirt", 29.99, R.drawable.tshirt));
            products.add(new Product("Jeans", 59.99, R.drawable.jeans));
            products.add(new Product("Jacket", 89.99, R.drawable.jacket));
            products.add(new Product("Dress", 79.99, R.drawable.dress));
            products.add(new Product("Shorts", 39.99, R.drawable.shorts));
            saveProductsToStorage();
        }
    }

    public List<Product> getProducts() {
        return new ArrayList<>(products);
    }

    public void addProduct(Product product) {
        products.add(product);
        saveProductsToStorage();
    }

    public void updateProduct(int index, Product product) {
        if (index >= 0 && index < products.size()) {
            products.set(index, product);
            saveProductsToStorage();
        }
    }

    public void deleteProduct(int index) {
        if (index >= 0 && index < products.size()) {
            products.remove(index);
            saveProductsToStorage();
        }
    }

    private void loadProducts() {
        try {
            FileInputStream fis = context.openFileInput(PRODUCT_FILE);
            ObjectInputStream ois = new ObjectInputStream(fis);
            products = (List<Product>) ois.readObject();
            ois.close();
            fis.close();
            Log.d(TAG, "Products loaded: " + products.size());
        } catch (Exception e) {
            Log.e(TAG, "Error loading products", e);
            products = new ArrayList<>();
        }
    }

    private void saveProductsToStorage() {
        try {
            FileOutputStream fos = context.openFileOutput(PRODUCT_FILE, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(products);
            oos.close();
            fos.close();
            Log.d(TAG, "Products saved to storage");
        } catch (Exception e) {
            Log.e(TAG, "Error saving products", e);
        }
    }
}