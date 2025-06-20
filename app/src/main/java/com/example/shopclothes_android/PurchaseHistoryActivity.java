package com.example.shopclothes_android;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PurchaseHistoryActivity extends AppCompatActivity implements
        PurchaseHistoryAdapter.OnPurchaseClickListener,
        PurchaseHistoryAdapter.OnLoadMoreListener {

    private MaterialToolbar toolbar;
    private RecyclerView rvPurchaseHistory;
    private TextView tvEmptyHistory;
    private View emptyHistoryContainer;
    private PurchaseManager purchaseManager;
    private PurchaseHistoryAdapter adapter;

    private static final int PAGE_SIZE = 10;
    private int currentPage = 0;
    private boolean isLoading = false;
    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase_history);

        purchaseManager = PurchaseManager.getInstance();
        purchaseManager.initialize(this);

        userEmail = FirebaseAuth.getInstance().getCurrentUser() != null
                ? FirebaseAuth.getInstance().getCurrentUser().getEmail()
                : "";

        initViews();
        setupToolbar();
        setupRecyclerView();
        loadInitialPurchases();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        rvPurchaseHistory = findViewById(R.id.rv_purchase_history);
        tvEmptyHistory = findViewById(R.id.tv_empty_history);
        emptyHistoryContainer = findViewById(R.id.empty_history_container);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Purchase History");
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        rvPurchaseHistory.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PurchaseHistoryAdapter(new ArrayList<>());
        adapter.setOnPurchaseClickListener(this);
        adapter.setOnLoadMoreListener(this);
        rvPurchaseHistory.setAdapter(adapter);

        // Add scroll listener for pagination
        rvPurchaseHistory.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager != null) {
                    int visibleItemCount = layoutManager.getChildCount();
                    int totalItemCount = layoutManager.getItemCount();
                    int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                            && firstVisibleItemPosition >= 0
                            && totalItemCount >= PAGE_SIZE) {
                        onLoadMore();
                    }
                }
            }
        });
    }

    private void loadInitialPurchases() {
        List<Purchase> initialPurchases = purchaseManager.getPurchasesByUserPaginated(userEmail, 0, PAGE_SIZE);

        if (initialPurchases.isEmpty()) {
            emptyHistoryContainer.setVisibility(View.VISIBLE);
            rvPurchaseHistory.setVisibility(View.GONE);
        } else {
            emptyHistoryContainer.setVisibility(View.GONE);
            rvPurchaseHistory.setVisibility(View.VISIBLE);
            adapter.addPurchases(initialPurchases);
            currentPage = 0;
        }
    }

    @Override
    public void onPurchaseClick(Purchase purchase) {
        // Navigate to purchase detail screen
        Intent intent = new Intent(this, PurchaseDetailActivity.class);
        intent.putExtra("purchase_id", purchase.getPurchaseId());
        startActivity(intent);
    }

    @Override
    public void onLoadMore() {
        if (!isLoading && purchaseManager.hasMorePurchases(userEmail, currentPage, PAGE_SIZE)) {
            isLoading = true;
            adapter.setLoading(true);

            // Simulate network delay
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                currentPage++;
                List<Purchase> morePurchases = purchaseManager.getPurchasesByUserPaginated(userEmail, currentPage,
                        PAGE_SIZE);
                adapter.addPurchases(morePurchases);
                adapter.setLoading(false);
                isLoading = false;
            }, 1000); // 1 second delay
        }
    }
}