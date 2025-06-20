package com.example.shopclothes_android;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import java.util.ArrayList;
import java.util.List;

public class ProductDashboardActivity extends AppCompatActivity
        implements ProductDashboardAdapter.OnProductActionListener, ProductEditDialogFragment.ProductEditListener {
    private RecyclerView rvProducts;
    private EditText etSearch;
    private ImageButton btnFilter;
    private View progressBar;
    private ProductDashboardAdapter adapter;
    private ProductManager productManager;
    private List<Product> allProducts = new ArrayList<>();
    private List<Product> filteredProducts = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_dashboard);

        productManager = ProductManager.getInstance();
        productManager.initialize(this);

        rvProducts = findViewById(R.id.rv_products);
        etSearch = findViewById(R.id.et_search);
        btnFilter = findViewById(R.id.btn_filter);
        progressBar = findViewById(R.id.progress_bar);

        rvProducts.setLayoutManager(new LinearLayoutManager(this));
        allProducts = productManager.getProducts();
        filteredProducts.addAll(allProducts);
        adapter = new ProductDashboardAdapter(filteredProducts, this);
        rvProducts.setAdapter(adapter);

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterProducts(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        btnFilter.setOnClickListener(v -> showFilterDialog());

        com.google.android.material.button.MaterialButton btnAddProduct = findViewById(R.id.btn_add_product);
        btnAddProduct.setOnClickListener(v -> {
            ProductEditDialogFragment dialog = ProductEditDialogFragment.newInstance(null, -1);
            dialog.show(getSupportFragmentManager(), "add_product");
        });

        // Setup bottom navigation
        com.google.android.material.bottomnavigation.BottomNavigationView bottomNav = findViewById(
                R.id.bottom_navigation);
        bottomNav.setSelectedItemId(R.id.nav_product_dashboard);
        bottomNav.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_product_dashboard)
                return true;
            if (item.getItemId() == R.id.nav_admin) {
                startActivity(new Intent(this, AdminDashboardActivity.class));
                finish();
                return true;
            }
            if (item.getItemId() == R.id.nav_settings) {
                startActivity(new Intent(this, SettingsActivity.class));
                finish();
                return true;
            }
            return false;
        });
    }

    private void filterProducts(String query) {
        filteredProducts.clear();
        if (query.isEmpty()) {
            filteredProducts.addAll(allProducts);
        } else {
            for (Product product : allProducts) {
                if (product.getName().toLowerCase().contains(query.toLowerCase())) {
                    filteredProducts.add(product);
                }
            }
        }
        adapter.setProducts(filteredProducts);
    }

    private void showFilterDialog() {
        Toast.makeText(this, "Filter feature coming soon!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onEditProduct(int position, Product product) {
        int realIndex = allProducts.indexOf(filteredProducts.get(position));
        ProductEditDialogFragment dialog = ProductEditDialogFragment.newInstance(product, realIndex);
        dialog.show(getSupportFragmentManager(), "edit_product");
    }

    @Override
    public void onDeleteProduct(int position) {
        int realIndex = allProducts.indexOf(filteredProducts.get(position));
        productManager.deleteProduct(realIndex);
        allProducts = productManager.getProducts();
        filterProducts(etSearch.getText().toString());
        Snackbar.make(rvProducts, "Product deleted", Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onProductSaved(Product product, int editIndex) {
        if (editIndex >= 0) {
            productManager.updateProduct(editIndex, product);
            Snackbar.make(rvProducts, "Product updated", Snackbar.LENGTH_SHORT).show();
        } else {
            productManager.addProduct(product);
            Snackbar.make(rvProducts, "Product added", Snackbar.LENGTH_SHORT).show();
        }
        allProducts = productManager.getProducts();
        filterProducts(etSearch.getText().toString());
    }
}