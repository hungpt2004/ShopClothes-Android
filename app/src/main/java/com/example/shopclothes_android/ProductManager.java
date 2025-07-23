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
    private static ProductManager instance;
    private ProductDatabaseHelper dbHelper;
    private Context context;

    private ProductManager() {}

    public static synchronized ProductManager getInstance() {
        if (instance == null) {
            instance = new ProductManager();
        }
        return instance;
    }

    public void initialize(Context context) {
        this.context = context;
        dbHelper = new ProductDatabaseHelper(context);
        // Insert default products if database is empty
        if (dbHelper.getAllProducts().isEmpty()) {
            dbHelper.addProduct(new Product("T-shirt", 29.99, R.drawable.tshirt));
            dbHelper.addProduct(new Product("Jeans", 59.99, R.drawable.jeans));
            dbHelper.addProduct(new Product("Jacket", 89.99, R.drawable.jacket));
            dbHelper.addProduct(new Product("Dress", 79.99, R.drawable.dress));
            dbHelper.addProduct(new Product("Shorts", 39.99, R.drawable.shorts));
        }
    }

    public List<Product> getProducts() {
        return dbHelper.getAllProducts();
    }

    public void addProduct(Product product) {
        dbHelper.addProduct(product);
    }

    public void updateProduct(int id, Product product) {
        dbHelper.updateProduct(id, product);
    }

    public void deleteProduct(int id) {
        dbHelper.deleteProduct(id);
    }

}