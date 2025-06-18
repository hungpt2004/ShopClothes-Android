package com.example.shopclothes_android;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private ImageView btnBack, btnSettings, btnEditAvatar;
    private CircleImageView imgAvatar;
    private TextView tvUserName, tvUserEmail, tvUserPhone, tvUserBirthDate,
            tvUserGender, tvUserAddress, btnManageAddress;
    private Button btnEditProfile, btnAddAddress;
    private LinearLayout layoutMyOrders, layoutWishlist, layoutNotifications,
            layoutHelpSupport, layoutLogout;
    private BottomNavigationView bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initViews();
        setupClickListeners();
        setupBottomNavigation();
    }

    private void initViews() {

        btnEditAvatar = findViewById(R.id.btnEditAvatar);
        imgAvatar = findViewById(R.id.imgAvatar);

        tvUserName = findViewById(R.id.tvUserName);
        tvUserEmail = findViewById(R.id.tvUserEmail);
        tvUserPhone = findViewById(R.id.tvUserPhone);
        tvUserBirthDate = findViewById(R.id.tvUserBirthDate);
        tvUserGender = findViewById(R.id.tvUserGender);


        btnEditProfile = findViewById(R.id.btnEditProfile);


        layoutMyOrders = findViewById(R.id.layoutMyOrders);
        layoutWishlist = findViewById(R.id.layoutWishlist);
        layoutLogout = findViewById(R.id.layoutLogout);

        bottomNavigation = findViewById(R.id.bottom_navigation);
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnSettings.setOnClickListener(v -> {
            // Navigate to settings
            // Intent intent = new Intent(ProfileActivity.this, SettingsActivity.class);
            // startActivity(intent);
        });

        btnEditAvatar.setOnClickListener(v -> {
            // Open image picker for avatar
            openImagePicker();
        });

        btnEditProfile.setOnClickListener(v -> {
            // Navigate to edit profile
            // Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
            // startActivity(intent);
        });

        btnManageAddress.setOnClickListener(v -> {
            // Navigate to address management
            // Intent intent = new Intent(ProfileActivity.this, AddressManagementActivity.class);
            // startActivity(intent);
        });

        btnAddAddress.setOnClickListener(v -> {
            // Navigate to add new address
            // Intent intent = new Intent(ProfileActivity.this, AddAddressActivity.class);
            // startActivity(intent);
        });

        layoutMyOrders.setOnClickListener(v -> {
            // Navigate to orders
            // Intent intent = new Intent(ProfileActivity.this, OrdersActivity.class);
            // startActivity(intent);
        });

        layoutWishlist.setOnClickListener(v -> {
            // Navigate to wishlist
            // Intent intent = new Intent(ProfileActivity.this, WishlistActivity.class);
            // startActivity(intent);
        });

        layoutNotifications.setOnClickListener(v -> {
            // Navigate to notifications settings
            // Intent intent = new Intent(ProfileActivity.this, NotificationsActivity.class);
            // startActivity(intent);
        });

        layoutHelpSupport.setOnClickListener(v -> {
            // Navigate to help & support
            // Intent intent = new Intent(ProfileActivity.this, HelpSupportActivity.class);
            // startActivity(intent);
        });

        layoutLogout.setOnClickListener(v -> {
            // Show logout confirmation dialog
            showLogoutDialog();
        });
    }

    private void openImagePicker() {
        // Implement image picker for avatar
        // You can use Intent to pick image from gallery or camera
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            // Set the selected image to avatar
            imgAvatar.setImageURI(data.getData());
        }
    }

    private void setupBottomNavigation() {
        bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                // Navigate to home
                Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                startActivity(intent);
                return true;

            } else if (itemId == R.id.nav_cart) {
                // Navigate to cart
                return true;
            } else if (itemId == R.id.nav_profile) {
                // Already in profile
                return true;
            }

            return false;
        });

        // Set profile as selected
        bottomNavigation.setSelectedItemId(R.id.nav_profile);
    }

    private void showLogoutDialog() {
        // Create and show logout confirmation dialog
        androidx.appcompat.app.AlertDialog.Builder builder =
                new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("Logout");
        builder.setMessage("Are you sure you want to logout?");
        builder.setPositiveButton("Yes", (dialog, which) -> {
            // Perform logout
            logout();
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> {
            dialog.dismiss();
        });
        builder.show();
    }

    private void logout() {
        // Clear user session/preferences
        // Navigate to login screen
        // Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
        // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        // startActivity(intent);
        // finish();
    }
}
