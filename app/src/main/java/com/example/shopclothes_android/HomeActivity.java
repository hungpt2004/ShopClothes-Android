package com.example.shopclothes_android;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity implements ProductAdapter.ProductClickListener {

    private ProductAdapter adapter;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        RecyclerView rvProducts = findViewById(R.id.rvProducts);
        rvProducts.setLayoutManager(new LinearLayoutManager(this));

        // Create sample products with real prices
        List<Product> products = new ArrayList<>();
        products.add(new Product("T-shirt", 29.99, R.drawable.tshirt));
        products.add(new Product("Jeans", 59.99, R.drawable.jeans));
        products.add(new Product("Jacket", 89.99, R.drawable.jacket));
        products.add(new Product("Dress", 79.99, R.drawable.dress));
        products.add(new Product("Shorts", 39.99, R.drawable.shorts));

        adapter = new ProductAdapter(products, this);
        rvProducts.setAdapter(adapter);

        // Setup bottom navigation
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_home);

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                return true;
            } else if (itemId == R.id.nav_cart) {
                startActivity(new Intent(HomeActivity.this, CartActivity.class));
                return true;
            } else if (itemId == R.id.nav_favorite) {
                startActivity(new Intent(HomeActivity.this, FavoritesActivity.class));
                return true;
            } else if (itemId == R.id.nav_profile) {
                // TODO: Implement profile activity
                return true;
            }
            return false;
        });
    }

    @Override
    public void onFavoriteClicked(Product product) {
        product.toggleFavorite();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onAddToCartClicked(Product product) {
        CartManager.getInstance().addToCart(product);
    }

    @Override
    public void onProductClicked(Product product) {
        try {
            Intent intent = new Intent(this, ProductDetailActivity.class);
            intent.putExtra("product_title", product.getName());
            intent.putExtra("product_description", "Mô tả sản phẩm đang cập nhật...");
            intent.putExtra("rating", 4.95f); // hoặc lấy từ product nếu có
            intent.putExtra("review_count", 22); // hoặc lấy từ product nếu có
            intent.putExtra("price", String.format("$%.2f", product.getPrice()));
            intent.putExtra("total_price", String.format("$%.2f", product.getTotalPrice()));
            intent.putExtra("product_image", product.getImageResId());
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "Error opening product detail: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
