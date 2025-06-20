package com.example.shopclothes_android;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class PurchaseDetailActivity extends AppCompatActivity {
    private MaterialToolbar toolbar;
    private TextView tvPurchaseId;
    private TextView tvPurchaseDate;
    private TextView tvSubtotal;
    private TextView tvShipping;
    private TextView tvTotal;
    private RecyclerView rvPurchaseItems;
    private PurchaseManager purchaseManager;
    private PurchaseDetailAdapter adapter;
    private String purchaseId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase_detail);

        purchaseId = getIntent().getStringExtra("purchase_id");
        if (purchaseId == null) {
            finish();
            return;
        }

        purchaseManager = PurchaseManager.getInstance();
        purchaseManager.initialize(this);

        initViews();
        setupToolbar();
        loadPurchaseDetails();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        tvPurchaseId = findViewById(R.id.tv_purchase_id);
        tvPurchaseDate = findViewById(R.id.tv_purchase_date);
        tvSubtotal = findViewById(R.id.tv_subtotal);
        tvShipping = findViewById(R.id.tv_shipping);
        tvTotal = findViewById(R.id.tv_total);
        rvPurchaseItems = findViewById(R.id.rv_purchase_items);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Purchase Details");
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void loadPurchaseDetails() {
        String userEmail = FirebaseAuth.getInstance().getCurrentUser() != null
                ? FirebaseAuth.getInstance().getCurrentUser().getEmail()
                : "";

        List<Purchase> userPurchases = purchaseManager.getPurchasesByUser(userEmail);
        Purchase purchase = null;

        for (Purchase p : userPurchases) {
            if (p.getPurchaseId().equals(purchaseId)) {
                purchase = p;
                break;
            }
        }

        if (purchase != null) {
            displayPurchaseDetails(purchase);
        } else {
            finish();
        }
    }

    private void displayPurchaseDetails(Purchase purchase) {
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("en", "US"));
        SimpleDateFormat dateFormatter = new SimpleDateFormat("MMM dd, yyyy 'at' HH:mm", Locale.getDefault());

        tvPurchaseId.setText("Order #" + purchase.getPurchaseId());
        tvPurchaseDate.setText(dateFormatter.format(purchase.getPurchaseDate()));
        tvSubtotal.setText(currencyFormatter.format(purchase.getSubtotal()));
        tvShipping.setText(currencyFormatter.format(purchase.getShippingFee()));
        tvTotal.setText(currencyFormatter.format(purchase.getTotal()));

        // Setup RecyclerView for purchase items
        rvPurchaseItems.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PurchaseDetailAdapter(purchase.getItems());
        rvPurchaseItems.setAdapter(adapter);
    }
}