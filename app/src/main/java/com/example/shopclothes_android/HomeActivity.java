package com.example.shopclothes_android;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HomeActivity extends AppCompatActivity implements ProductAdapter.ProductClickListener {
    private androidx.viewpager2.widget.ViewPager2 bannerViewPager;
    private LinearLayout bannerIndicator;
    private BannerAdapter bannerAdapter;
    private final List<BannerAdapter.BannerItem> bannerList = Arrays.asList(
            new BannerAdapter.BannerItem(
                    "https://i.pinimg.com/736x/54/30/f6/5430f68f39db5981c738f0deb1ce2838.jpg",
                    "Summer Sale", "Up to 50% Off"
            ),
            new BannerAdapter.BannerItem(
                    "https://i.pinimg.com/1200x/70/54/4e/70544ed5150760c3c8d8b6c5344972e5.jpg",
                    "New Arrivals", "Trendy & Stylish"
            ),
            new BannerAdapter.BannerItem(
                    "https://i.pinimg.com/736x/aa/fa/e9/aafae976a38bd341fa0d1d6f57a52d6e.jpg",
                    "Best Seller", "Hot Items This Week"
            )
    );

    private ProductAdapter adapter;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        bannerViewPager = findViewById(R.id.bannerViewPager);
        bannerIndicator = findViewById(R.id.bannerIndicator);
        bannerAdapter = new BannerAdapter(this, bannerList);
        bannerViewPager.setAdapter(bannerAdapter);

        // Dot indicator setup
        setupBannerDots(bannerList.size(), 0);
        bannerViewPager.registerOnPageChangeCallback(new androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                setupBannerDots(bannerList.size(), position);
            }
        });

        // Auto-slide
        final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                int next = bannerViewPager.getCurrentItem() + 1;
                if (next >= bannerAdapter.getItemCount()) next = 0;
                bannerViewPager.setCurrentItem(next, true);
                handler.postDelayed(this, 3500);
            }
        };
        handler.postDelayed(runnable, 3500);

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

    private void setupBannerDots(int count, int selected) {
        LinearLayout indicatorLayout = (LinearLayout) findViewById(R.id.bannerIndicator);
        indicatorLayout.removeAllViews();
        for (int i = 0; i < count; i++) {
            View dot = new View(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    i == selected ? 10 : 8,
                    i == selected ? 10 : 8
            );
            params.setMargins(8, 0, 8, 0);
            dot.setLayoutParams(params);
            dot.setBackgroundResource(i == selected ? R.drawable.dot_indicator_selected : R.drawable.dot_indicator_unselected);
            indicatorLayout.addView(dot);
        }
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
