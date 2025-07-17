package com.example.shopclothes_android;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.ImageView;
import com.google.android.gms.wallet.Wallet;
import com.google.android.gms.wallet.WalletConstants;
import com.google.android.gms.wallet.PaymentsClient;
import com.google.android.gms.wallet.PaymentData;
import com.google.android.gms.wallet.AutoResolveHelper;
import com.google.android.material.button.MaterialButton;
import android.util.Log;
import com.google.android.gms.tasks.Task;
import org.json.JSONObject;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class CartActivity extends AppCompatActivity implements CartAdapter.CartItemListener {
    private static final int LOAD_PAYMENT_DATA_REQUEST_CODE = 991;

    private PaymentsClient paymentsClient;
    private ImageView imgGPayMark;

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
        imgGPayMark = findViewById(R.id.img_gpay_mark);

        // Google Pay button
        MaterialButton btnGooglePay = findViewById(R.id.btn_google_pay);
        btnGooglePay.setVisibility(View.GONE); // Hide by default, show if available

        btnGooglePay.setOnClickListener(v -> launchGooglePay());

        // Setup Google Pay API client
        paymentsClient = Wallet.getPaymentsClient(
            this,
            new Wallet.WalletOptions.Builder()
                .setEnvironment(WalletConstants.ENVIRONMENT_TEST)
                .build()
        );

        // Check if Google Pay is available and show/hide the mark
        isReadyToPay();

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

    private void launchGooglePay() {
        try {
            JSONObject paymentDataRequestJson = getPaymentDataRequest();
            if (paymentDataRequestJson == null) {
                Log.e("CartActivity", "PaymentDataRequest JSON is null");
                return;
            }
            com.google.android.gms.wallet.PaymentDataRequest request =
                    com.google.android.gms.wallet.PaymentDataRequest.fromJson(paymentDataRequestJson.toString());
            Task<PaymentData> task = paymentsClient.loadPaymentData(request);
            AutoResolveHelper.resolveTask(task, this, LOAD_PAYMENT_DATA_REQUEST_CODE);
        } catch (Exception e) {
            Log.e("CartActivity", "Error launching Google Pay", e);
        }
    }

    private JSONObject getPaymentDataRequest() {
        try {
            JSONObject paymentDataRequest = new JSONObject();
            paymentDataRequest.put("apiVersion", 2);
            paymentDataRequest.put("apiVersionMinor", 0);

            // Allowed payment methods
            JSONObject cardPaymentMethod = new JSONObject();
            cardPaymentMethod.put("type", "CARD");
            JSONObject parameters = new JSONObject();
            parameters.put("allowedAuthMethods", new org.json.JSONArray().put("PAN_ONLY").put("CRYPTOGRAM_3DS"));
            parameters.put("allowedCardNetworks", new org.json.JSONArray().put("MASTERCARD").put("VISA"));
            parameters.put("billingAddressRequired", true);
            JSONObject billingAddressParams = new JSONObject();
            billingAddressParams.put("format", "FULL");
            parameters.put("billingAddressParameters", billingAddressParams);
            cardPaymentMethod.put("parameters", parameters);
            JSONObject tokenizationSpec = new JSONObject();
            tokenizationSpec.put("type", "PAYMENT_GATEWAY");
            JSONObject tokenizationParams = new JSONObject();
            tokenizationParams.put("gateway", "example"); // Replace with your gateway
            tokenizationParams.put("gatewayMerchantId", "exampleGatewayMerchantId"); // Replace with your merchant ID
            tokenizationSpec.put("parameters", tokenizationParams);
            cardPaymentMethod.put("tokenizationSpecification", tokenizationSpec);

            paymentDataRequest.put("allowedPaymentMethods", new org.json.JSONArray().put(cardPaymentMethod));

            // Transaction info
            JSONObject transactionInfo = new JSONObject();
            transactionInfo.put("totalPrice", String.format(Locale.US, "%.2f", cartManager.getTotal()));
            transactionInfo.put("totalPriceStatus", "FINAL");
            transactionInfo.put("currencyCode", "USD");
            paymentDataRequest.put("transactionInfo", transactionInfo);

            // Merchant info
            JSONObject merchantInfo = new JSONObject();
            merchantInfo.put("merchantName", "Example Merchant"); // Replace with your merchant name
            paymentDataRequest.put("merchantInfo", merchantInfo);

            return paymentDataRequest;
        } catch (Exception e) {
            Log.e("CartActivity", "Error building PaymentDataRequest", e);
            return null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LOAD_PAYMENT_DATA_REQUEST_CODE) {
            switch (resultCode) {
                case RESULT_OK:
                    if (data != null) {
                        PaymentData paymentData = PaymentData.getFromIntent(data);
                        handlePaymentSuccess(paymentData);
                    }
                    break;
                case RESULT_CANCELED:
                    // User canceled
                    break;
                case AutoResolveHelper.RESULT_ERROR:
                    if (data != null) {
                        com.google.android.gms.common.api.Status status = AutoResolveHelper.getStatusFromIntent(data);
                        Log.e("CartActivity", "Google Pay failed: " + (status != null ? status.getStatusMessage() : ""));
                    }
                    break;
            }
        }
    }

    private void handlePaymentSuccess(PaymentData paymentData) {
        if (paymentData == null) return;
        String paymentInfo = paymentData.toJson();
        // You should send paymentInfo to your server for verification and fulfillment
        // For demo, just show success and clear cart
        showPaymentSuccess();
    }

    private void showPaymentSuccess() {
        // Clear cart after successful payment
        cartManager.clearCart();
        Intent intent = new Intent(CartActivity.this, PaymentSuccessActivity.class);
        intent.putExtra("total_amount", currencyFormatter.format(0));
        startActivity(intent);
        finish();
    }

    // Check if Google Pay is available on this device
    private void isReadyToPay() {
        try {
            JSONObject isReadyToPayJson = new JSONObject()
                .put("allowedPaymentMethods", new org.json.JSONArray()
                    .put(new JSONObject()
                        .put("type", "CARD")
                        .put("parameters", new JSONObject()
                            .put("allowedAuthMethods", new org.json.JSONArray().put("PAN_ONLY").put("CRYPTOGRAM_3DS"))
                            .put("allowedCardNetworks", new org.json.JSONArray().put("MASTERCARD").put("VISA"))
                        )
                    )
                );
            Task<Boolean> task = paymentsClient.isReadyToPay(new com.google.android.gms.wallet.IsReadyToPayRequest.Builder().fromJson(isReadyToPayJson.toString()).build());
            task.addOnCompleteListener(this, completedTask -> {
                boolean result = false;
                try {
                    result = completedTask.getResult(Exception.class);
                } catch (Exception e) {
                    result = false;
                }
                imgGPayMark.setVisibility(result ? View.VISIBLE : View.GONE);
                MaterialButton btnGooglePay = findViewById(R.id.btn_google_pay);
                btnGooglePay.setVisibility(result ? View.VISIBLE : View.GONE);
            });
        } catch (Exception e) {
            imgGPayMark.setVisibility(View.GONE);
            MaterialButton btnGooglePay = findViewById(R.id.btn_google_pay);
            btnGooglePay.setVisibility(View.GONE);
        }
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
