package com.example.shopclothes_android;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.ArrayList;
import java.util.List;

public class FavoritesActivity extends AppCompatActivity implements ProductAdapter.ProductClickListener {

    private View emptyFavoritesContainer;
    private RecyclerView rvFavorites;
    private ProductAdapter adapter;
    private FavoriteManager favoriteManager;
    private List<Product> currentProducts = new ArrayList<>();
    private boolean isLoading = false;
    private static final int PAGE_SIZE = 10;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        favoriteManager = FavoriteManager.getInstance();

        // Initialize views
        emptyFavoritesContainer = findViewById(R.id.empty_favorites_container);
        rvFavorites = findViewById(R.id.rv_favorites);
        Button btnBrowseProducts = findViewById(R.id.btn_browse_shopping);

        // Setup RecyclerView with LinearLayoutManager
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rvFavorites.setLayoutManager(layoutManager);

        // Initialize adapter with empty list
        adapter = new ProductAdapter(currentProducts, this);
        rvFavorites.setAdapter(adapter);

        // Add scroll listener for pagination
        rvFavorites.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (!isLoading) {
                    int visibleItemCount = layoutManager.getChildCount();
                    int totalItemCount = layoutManager.getItemCount();
                    int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                            && firstVisibleItemPosition >= 0
                            && totalItemCount < favoriteManager.getFavoriteProducts().size()) {
                        loadMoreItems();
                    }
                }
            }
        });

        // Initial load
        loadInitialItems();

        // Browse products button click handler
        btnBrowseProducts.setOnClickListener(v -> {
            Intent intent = new Intent(FavoritesActivity.this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

        // Setup bottom navigation
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_favorite);

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                startActivity(new Intent(FavoritesActivity.this, HomeActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.nav_cart) {
                startActivity(new Intent(FavoritesActivity.this, CartActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.nav_favorite) {
                return true;
            } else if (itemId == R.id.nav_profile) {
                // TODO: Implement profile activity
                return true;
            }
            return false;
        });
    }

    private void loadInitialItems() {
        isLoading = true;
        List<Product> allFavorites = favoriteManager.getFavoriteProducts();

        if (allFavorites.isEmpty()) {
            showEmptyState();
            isLoading = false;
            return;
        }

        currentProducts.clear();
        int itemsToLoad = Math.min(PAGE_SIZE, allFavorites.size());
        for (int i = 0; i < itemsToLoad; i++) {
            currentProducts.add(allFavorites.get(i));
        }

        adapter.notifyDataSetChanged();
        showContent();
        isLoading = false;
    }

    private void loadMoreItems() {
        isLoading = true;
        List<Product> allFavorites = favoriteManager.getFavoriteProducts();

        int currentSize = currentProducts.size();
        int remainingItems = allFavorites.size() - currentSize;

        if (remainingItems <= 0) {
            isLoading = false;
            return;
        }

        int itemsToLoad = Math.min(PAGE_SIZE, remainingItems);
        for (int i = 0; i < itemsToLoad; i++) {
            currentProducts.add(allFavorites.get(currentSize + i));
        }

        adapter.notifyItemRangeInserted(currentSize, itemsToLoad);
        isLoading = false;
    }

    private void showEmptyState() {
        emptyFavoritesContainer.setVisibility(View.VISIBLE);
        rvFavorites.setVisibility(View.GONE);
    }

    private void showContent() {
        emptyFavoritesContainer.setVisibility(View.GONE);
        rvFavorites.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadInitialItems();
    }

    @Override
    public void onFavoriteClicked(Product product) {
        product.toggleFavorite();
        // Remove the product from the current list
        currentProducts.remove(product);
        adapter.notifyDataSetChanged();

        if (currentProducts.isEmpty()) {
            showEmptyState();
        }
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