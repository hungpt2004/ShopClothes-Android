package com.example.shopclothes_android;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.Observer;
import com.example.shopclothes_android.R;

public class ProductDetailActivity extends AppCompatActivity {

    // ViewModel để quản lý state
    private ProductDetailViewModel viewModel;

    // UI Components
    private ImageButton btnBack, btnShare, btnBookmark, btnMore;
    private ImageView imgProduct, imgSellerAvatar;
    private TextView tvImageCounter, tvRating, tvReviewCount, tvProductTitle;
    private TextView tvBeds, tvBath, tvSize, tvPeople;
    private TextView tvSellerName, tvSellerDescription, tvDescription, tvMore;
    private TextView tvPrice, tvTotalPrice;
    private Button btnBook;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        // Khởi tạo ViewModel
        viewModel = new ViewModelProvider(this).get(ProductDetailViewModel.class);

        // Khởi tạo views
        initViews();

        // Thiết lập observers cho state changes
        setupObservers();

        // Thiết lập click listeners
        setupClickListeners();

        // Load dữ liệu từ Intent
        loadDataFromIntent();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btn_back);
        btnShare = findViewById(R.id.btn_share);
        btnBookmark = findViewById(R.id.btn_bookmark);
        btnMore = findViewById(R.id.btn_more);
        imgProduct = findViewById(R.id.img_product);
        tvImageCounter = findViewById(R.id.tv_image_counter);
        tvRating = findViewById(R.id.tv_rating);
        tvReviewCount = findViewById(R.id.tv_review_count);
        tvProductTitle = findViewById(R.id.tv_product_title);
        tvBeds = findViewById(R.id.tv_trouser);
        tvBath = findViewById(R.id.tv_bath);
        tvSize = findViewById(R.id.tv_size);
        tvPeople = findViewById(R.id.tv_people);
        imgSellerAvatar = findViewById(R.id.img_seller_avatar);
        tvSellerName = findViewById(R.id.tv_seller_name);
        tvSellerDescription = findViewById(R.id.tv_seller_description);
        tvDescription = findViewById(R.id.tv_description);
        tvMore = findViewById(R.id.tv_more);
        tvPrice = findViewById(R.id.tv_price);
        tvTotalPrice = findViewById(R.id.tv_total_price);
        btnBook = findViewById(R.id.btn_book);
    }

    /**
     * Thiết lập observers để lắng nghe state changes
     */
    private void setupObservers() {
        // Observer cho product state
        viewModel.getProductState().observe(this, new Observer<ProductDetailViewModel.ProductState>() {
            @Override
            public void onChanged(ProductDetailViewModel.ProductState productState) {
                if (productState != null) {
                    updateProductUI(productState);
                }
            }
        });

        // Observer cho bookmark state
        viewModel.getIsBookmarked().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isBookmarked) {
                updateBookmarkUI(isBookmarked);
            }
        });

        // Observer cho description expanded state
        viewModel.getIsDescriptionExpanded().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isExpanded) {
                updateDescriptionUI(isExpanded);
            }
        });

        // Observer cho loading state
        viewModel.getIsLoading().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isLoading) {
                // Hiển thị/ẩn loading indicator
                if (isLoading) {
                    // Show loading
                } else {
                    // Hide loading
                }
            }
        });
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> onBackPressed());

        btnShare.setOnClickListener(v -> shareProduct());

        btnBookmark.setOnClickListener(v -> {
            viewModel.toggleBookmark();
        });

        btnMore.setOnClickListener(v -> showMoreOptions());

        tvMore.setOnClickListener(v -> {
            viewModel.toggleDescription();
        });

        btnBook.setOnClickListener(v -> bookProduct());

        imgProduct.setOnClickListener(v -> openImageGallery());
    }

    /**
     * Load dữ liệu từ Intent và cập nhật ViewModel
     */
    private void loadDataFromIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            String title = intent.getStringExtra("product_title");
            String description = intent.getStringExtra("product_description");
            float rating = intent.getFloatExtra("rating", 4.95f);
            int reviewCount = intent.getIntExtra("review_count", 22);
            String price = intent.getStringExtra("price");
            String totalPrice = intent.getStringExtra("total_price");

            // Update ViewModel với data từ Intent
            viewModel.loadProductData(title, description, rating, reviewCount, price, totalPrice);
        }
    }

    /**
     * Cập nhật UI khi product state thay đổi
     */
    private void updateProductUI(ProductDetailViewModel.ProductState state) {
        tvProductTitle.setText(state.title);
        tvDescription.setText(state.description);
        tvRating.setText(String.valueOf(state.rating));
        tvReviewCount.setText(state.reviewCount + " reviews");
        tvPrice.setText(state.price);
        tvTotalPrice.setText(state.totalPrice);
    }

    /**
     * Cập nhật UI khi bookmark state thay đổi
     */
    private void updateBookmarkUI(Boolean isBookmarked) {
        if (isBookmarked) {
            btnBookmark.setImageResource(R.drawable.ic_bookmark_filled);
            Toast.makeText(this, "Added to bookmarks", Toast.LENGTH_SHORT).show();
            saveBookmarkToDatabase();
        } else {
            btnBookmark.setImageResource(R.drawable.ic_bookmark);
            Toast.makeText(this, "Removed from bookmarks", Toast.LENGTH_SHORT).show();
            removeBookmarkFromDatabase();
        }
    }

    /**
     * Cập nhật UI khi description expanded state thay đổi
     */
    private void updateDescriptionUI(Boolean isExpanded) {
        if (isExpanded) {
            tvDescription.setMaxLines(Integer.MAX_VALUE);
            tvMore.setText("Less");
        } else {
            tvDescription.setMaxLines(3);
            tvMore.setText("More");
        }
    }

    // Các method khác giữ nguyên
    private void shareProduct() {
        ProductDetailViewModel.ProductState state = viewModel.getProductState().getValue();
        if (state != null) {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            String shareText = "Check out this amazing product: " + state.title + "\nPrice: " + state.price;
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
            startActivity(Intent.createChooser(shareIntent, "Share Product"));
        }
    }

    private void showMoreOptions() {
        Toast.makeText(this, "More options clicked", Toast.LENGTH_SHORT).show();
    }

    private void bookProduct() {
        if (!isUserLoggedIn()) {
            // Thay vì chuyển đến LoginActivity, hiển thị Toast thông báo
            Toast.makeText(this, "Please login to book this product", Toast.LENGTH_LONG).show();
            return;
        }

        ProductDetailViewModel.ProductState state = viewModel.getProductState().getValue();
        if (state != null) {
            // Thay vì chuyển đến CheckoutActivity, hiển thị Toast thông báo
            Toast.makeText(this, "Booking: " + state.title + " - " + state.price, Toast.LENGTH_LONG).show();
            // Có thể thêm dialog confirmation hoặc bottom sheet
            showBookingConfirmation(state);
        }
    }

    private void openImageGallery() {
        // Thay vì chuyển đến ImageGalleryActivity, hiển thị Toast thông báo
        Toast.makeText(this, "Image gallery feature coming soon!", Toast.LENGTH_SHORT).show();
        // Có thể mở một dialog hiển thị hình ảnh lớn hơn
    }

    /**
     * Hiển thị dialog xác nhận booking (thay thế cho CheckoutActivity tạm thời)
     */
    private void showBookingConfirmation(ProductDetailViewModel.ProductState state) {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Confirm Booking")
                .setMessage("Do you want to book: " + state.title + "\nPrice: " + state.price + "\nTotal: " + state.totalPrice)
                .setPositiveButton("Confirm", (dialog, which) -> {
                    Toast.makeText(this, "Booking confirmed! (Demo)", Toast.LENGTH_LONG).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private boolean isUserLoggedIn() {
        return true; // Tạm thời return true
    }

    private void saveBookmarkToDatabase() {
        // Implement save to database logic
    }

    private void removeBookmarkFromDatabase() {
        // Implement remove from database logic
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}