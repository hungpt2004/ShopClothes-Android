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

    // Lưu thông tin sản phẩm hiện tại để thêm vào cart
    private Product currentProduct;

    // UI Components
    private ImageButton btnBack, btnShare, btnBookmark, btnMore;
    private ImageView imgProduct, imgSellerAvatar;
    private TextView tvImageCounter, tvRating, tvReviewCount, tvProductTitle;
    private TextView tvTrouser, tvBath, tvSize, tvPeople;
    private TextView tvSellerName, tvSellerDescription;
    private TextView tvDescription, tvMore;
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
        try {
            btnBack = findViewById(R.id.btn_back);
            btnShare = findViewById(R.id.btn_share);
            btnBookmark = findViewById(R.id.btn_bookmark);
            btnMore = findViewById(R.id.btn_more);
            imgProduct = findViewById(R.id.img_product);
            tvImageCounter = findViewById(R.id.tv_image_counter);
            tvRating = findViewById(R.id.tv_rating);
            tvReviewCount = findViewById(R.id.tv_review_count);
            tvProductTitle = findViewById(R.id.tv_product_title);
            tvTrouser = findViewById(R.id.tv_trouser);
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
        } catch (Exception e) {
            Toast.makeText(this, "Error initializing views: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            finish();
        }
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
        try {
            if (btnBack != null) btnBack.setOnClickListener(v -> {
                Toast.makeText(this, "Back button clicked", Toast.LENGTH_SHORT).show();
                onBackPressed();
            });

            if (btnShare != null) btnShare.setOnClickListener(v -> shareProduct());

            if (btnBookmark != null) btnBookmark.setOnClickListener(v -> {
                viewModel.toggleBookmark();
            });

            if (btnMore != null) btnMore.setOnClickListener(v -> showMoreOptions());

            if (tvMore != null) tvMore.setOnClickListener(v -> {
                viewModel.toggleDescription();
            });

            if (btnBook != null) btnBook.setOnClickListener(v -> bookProduct());

            if (imgProduct != null) imgProduct.setOnClickListener(v -> openImageGallery());
        } catch (Exception e) {
            Toast.makeText(this, "Error setting up click listeners: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
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
            int imageResId = intent.getIntExtra("product_image", R.drawable.placeholder_product);

            // Đặt giá trị mặc định nếu null
            if (title == null) title = "Product Title";
            if (description == null) description = "Product description...";
            if (price == null) price = "$0.00";
            if (totalPrice == null) totalPrice = "$0.00";

            // Tạo Product object từ dữ liệu Intent để có thể thêm vào cart
            try {
                // Lấy giá từ string (loại bỏ ký tự $)
                double productPrice = Double.parseDouble(price.replace("$", ""));
                currentProduct = new Product(title, productPrice, imageResId);
            } catch (Exception e) {
                currentProduct = new Product(title, 0.0, imageResId);
            }

            // Cập nhật ảnh sản phẩm
            if (imgProduct != null) {
                try {
                    imgProduct.setImageResource(imageResId);
                } catch (Exception e) {
                    imgProduct.setImageResource(R.drawable.placeholder_product);
                }
            }

            // Update ViewModel với data từ Intent
            viewModel.loadProductData(title, description, rating, reviewCount, price, totalPrice);
        }
    }

    /**
     * Cập nhật UI khi product state thay đổi
     */
    private void updateProductUI(ProductDetailViewModel.ProductState state) {
        if (tvProductTitle != null) tvProductTitle.setText(state.title);
        if (tvDescription != null) tvDescription.setText(state.description);
        if (tvRating != null) tvRating.setText(String.valueOf(state.rating));
        if (tvReviewCount != null) tvReviewCount.setText(state.reviewCount + " reviews");
        if (tvPrice != null) tvPrice.setText(state.price);
        if (tvTotalPrice != null) tvTotalPrice.setText(state.totalPrice);
    }

    /**
     * Cập nhật UI khi bookmark state thay đổi
     */
    private void updateBookmarkUI(Boolean isBookmarked) {
        if (btnBookmark != null) {
            try {
                if (isBookmarked) {
                    btnBookmark.setImageResource(R.drawable.ic_bookmark_filled);
                    Toast.makeText(this, "Added to bookmarks", Toast.LENGTH_SHORT).show();
                    saveBookmarkToDatabase();
                } else {
                    btnBookmark.setImageResource(R.drawable.ic_bookmark);
                    Toast.makeText(this, "Removed from bookmarks", Toast.LENGTH_SHORT).show();
                    removeBookmarkFromDatabase();
                }
            } catch (Exception e) {
                Toast.makeText(this, "Error updating bookmark UI: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
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
        if (currentProduct == null) {
            Toast.makeText(this, "Product information not available", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // Thêm sản phẩm vào cart
            CartManager.getInstance().addToCart(currentProduct);
            
            // Hiển thị thông báo thành công
            Toast.makeText(this, "Đã thêm " + currentProduct.getName() + " vào giỏ hàng!", Toast.LENGTH_SHORT).show();
            
            // Hiển thị dialog xác nhận với tùy chọn đi đến cart
            showAddToCartConfirmation();
            
        } catch (Exception e) {
            Toast.makeText(this, "Error adding to cart: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void openImageGallery() {
        // Thay vì chuyển đến ImageGalleryActivity, hiển thị Toast thông báo
        Toast.makeText(this, "Image gallery feature coming soon!", Toast.LENGTH_SHORT).show();
        // Có thể mở một dialog hiển thị hình ảnh lớn hơn
    }

    /**
     * Hiển thị dialog xác nhận sau khi thêm vào cart
     */
    private void showAddToCartConfirmation() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Đã thêm vào giỏ hàng")
                .setMessage("Sản phẩm đã được thêm vào giỏ hàng thành công!\nBạn có muốn xem giỏ hàng không?")
                .setPositiveButton("Xem giỏ hàng", (dialog, which) -> {
                    // Chuyển đến CartActivity
                    Intent intent = new Intent(this, CartActivity.class);
                    startActivity(intent);
                })
                .setNegativeButton("Tiếp tục mua sắm", null)
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