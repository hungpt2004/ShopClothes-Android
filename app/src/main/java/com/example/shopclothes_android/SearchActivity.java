package com.example.shopclothes_android;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity implements ProductAdapter.ProductClickListener {
    private ProductAdapter adapter;
    private List<Product> allProducts;
    private List<Product> filteredProducts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // Xử lý nút back trên toolbar
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        EditText etSearch = findViewById(R.id.etSearch);
        RecyclerView rvProducts = findViewById(R.id.rvProducts);
        rvProducts.setLayoutManager(new LinearLayoutManager(this));

        // Sample data (có thể lấy từ intent hoặc static)
        allProducts = new ArrayList<>();
        allProducts.add(new Product("T-shirt", 29.99, R.drawable.tshirt));
        allProducts.add(new Product("Jeans", 59.99, R.drawable.jeans));
        allProducts.add(new Product("Jacket", 89.99, R.drawable.jacket));
        allProducts.add(new Product("Dress", 79.99, R.drawable.dress));
        allProducts.add(new Product("Shorts", 39.99, R.drawable.shorts));

        filteredProducts = new ArrayList<>(allProducts);
        adapter = new ProductAdapter(filteredProducts, this);
        rvProducts.setAdapter(adapter);

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterProducts(s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void filterProducts(String query) {
        filteredProducts.clear();
        if (query.isEmpty()) {
            filteredProducts.addAll(allProducts);
        } else {
            for (Product p : allProducts) {
                if (p.getName().toLowerCase().contains(query.toLowerCase())) {
                    filteredProducts.add(p);
                }
            }
        }
        adapter.notifyDataSetChanged();
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
            intent.putExtra("product_description", "Sản phẩm chất lượng cao với thiết kế hiện đại, phù hợp cho mọi nhu cầu sử dụng. Cam kết mang đến trải nghiệm tuyệt vời cho khách hàng.");
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
