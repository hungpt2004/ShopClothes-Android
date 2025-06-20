package com.example.shopclothes_android;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class CartActivity extends AppCompatActivity implements CartAdapter.CartItemListener {

    private RecyclerView rvCart;
    private TextView tvSubtotal, tvShipping, tvTotal;
    private Button btnCheckout;
    private CartManager cartManager;
    private View emptyCartContainer;
    private View cartContentContainer;
    private Button btnContinueShopping;
    private CartAdapter cartAdapter;
    private BottomNavigationView bottomNavigationView;
    private final NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("en", "US"));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        cartManager = CartManager.getInstance();

        // Initialize views
        rvCart = findViewById(R.id.lvCart);
        tvSubtotal = findViewById(R.id.tv_subtotal);
        tvShipping = findViewById(R.id.tv_shipping);
        tvTotal = findViewById(R.id.tv_total);
        btnCheckout = findViewById(R.id.btn_checkout);
        emptyCartContainer = findViewById(R.id.empty_cart_container);
        cartContentContainer = findViewById(R.id.cart_content_container);
        btnContinueShopping = findViewById(R.id.btn_continue_shopping);

        // Setup RecyclerView
        rvCart.setLayoutManager(new LinearLayoutManager(this));

        // Get cart items
        List<Product> cartItems = cartManager.getCartItems();

        // Create adapter for cart items
        cartAdapter = new CartAdapter(cartItems, this);
        rvCart.setAdapter(cartAdapter);

        // Update UI based on cart state
        updateCartState(cartItems);

        // Continue shopping button click handler
        btnContinueShopping.setOnClickListener(v -> {
            Intent intent = new Intent(CartActivity.this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

        // Checkout button click handler
        btnCheckout.setOnClickListener(v -> {
            if (cartItems.isEmpty()) {
                showEmptyCartDialog();
            } else {
                showConfirmationDialog();
            }
        });

        // Setup bottom navigation
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_cart);

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                startActivity(new Intent(CartActivity.this, HomeActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.nav_cart) {
                return true;
            } else if (itemId == R.id.nav_favorite) {
                startActivity(new Intent(CartActivity.this, FavoritesActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.nav_profile) {
                startActivity(new Intent(CartActivity.this, ProfileActivity.class));
                finish();
                return true;
            }
            return false;
        });
    }

    @Override
    public void onQuantityChanged() {
        updatePrices();
    }

    @Override
    public void onItemRemoved(Product product) {
        new AlertDialog.Builder(this)
                .setTitle("Xóa sản phẩm")
                .setMessage("Bạn có chắc chắn muốn xóa " + product.getName() + " khỏi giỏ hàng?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    cartManager.getCartItems().remove(product);
                    cartAdapter.notifyDataSetChanged();
                    updateCartState(cartManager.getCartItems());
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void updateCartState(List<Product> cartItems) {
        if (cartItems.isEmpty()) {
            // Show empty cart view
            emptyCartContainer.setVisibility(View.VISIBLE);
            cartContentContainer.setVisibility(View.GONE);
        } else {
            // Show cart content
            emptyCartContainer.setVisibility(View.GONE);
            cartContentContainer.setVisibility(View.VISIBLE);
            // Update prices
            updatePrices();
        }
    }

    private void updatePrices() {
        double subtotal = cartManager.getSubtotal();
        double shipping = cartManager.getShippingFee();
        double total = cartManager.getTotal();

        tvSubtotal.setText(currencyFormatter.format(subtotal));
        tvShipping.setText(currencyFormatter.format(shipping));
        tvTotal.setText(currencyFormatter.format(total));
    }

    private void showEmptyCartDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Giỏ hàng trống")
                .setMessage("Vui lòng thêm sản phẩm vào giỏ hàng trước khi thanh toán.")
                .setPositiveButton("OK", null)
                .show();
    }

    private void showConfirmationDialog() {
        String totalAmount = currencyFormatter.format(cartManager.getTotal());

        new AlertDialog.Builder(this)
                .setTitle("Xác nhận thanh toán")
                .setMessage("Bạn có chắc chắn muốn thanh toán đơn hàng với tổng tiền " + totalAmount + "?")
                .setPositiveButton("Xác nhận", (dialog, which) -> {
                    // Save purchase to internal storage
                    savePurchase();

                    // Clear cart after successful payment
                    cartManager.clearCart();

                    // Navigate to success screen
                    Intent intent = new Intent(CartActivity.this, PaymentSuccessActivity.class);
                    intent.putExtra("total_amount", totalAmount);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void savePurchase() {
        List<Product> cartItems = cartManager.getCartItems();
        if (!cartItems.isEmpty()) {
            PurchaseManager purchaseManager = PurchaseManager.getInstance();
            purchaseManager.initialize(this);

            String userEmail = com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser() != null
                    ? com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser().getEmail()
                    : "guest@example.com";

            purchaseManager.savePurchase(
                    cartItems,
                    cartManager.getSubtotal(),
                    cartManager.getShippingFee(),
                    cartManager.getTotal(),
                    userEmail);
        }
    }
}
