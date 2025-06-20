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

        ProductManager productManager = ProductManager.getInstance();
        productManager.initialize(this);
        List<Product> products = productManager.getProducts();

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
                startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
                return true;
            }
            return false;
        });

        // Sự kiện mở SearchActivity khi click vào search bar
        findViewById(R.id.search_container).setOnClickListener(v -> {
            startActivity(new Intent(HomeActivity.this, SearchActivity.class));
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
            intent.putExtra("product_description",
                    "Sản phẩm chất lượng cao với thiết kế hiện đại, phù hợp cho mọi nhu cầu sử dụng. Cam kết mang đến trải nghiệm tuyệt vời cho khách hàng.");
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
