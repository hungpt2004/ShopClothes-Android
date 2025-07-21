package com.example.shopclothes_android;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;
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
        loadUserData();
    }

    private void initViews() {
//        btnBack = findViewById(R.id.btnBack);
//        btnSettings = findViewById(R.id.btnSettings);
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

    private void loadUserData() {
        // Load user data from SharedPreferences or database
        // For now, we'll use sample data
        tvUserName.setText("Nguyễn Văn An");
        tvUserEmail.setText("nguyenvanan@gmail.com");
        tvUserPhone.setText("0123 456 789");
        tvUserBirthDate.setText("15/03/1995");
        tvUserGender.setText("Nam");
        tvUserAddress.setText("Nguyễn Văn An");
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnSettings.setOnClickListener(v -> {
            // Navigate to settings
            Toast.makeText(this, "Settings feature coming soon!", Toast.LENGTH_SHORT).show();
            // Intent intent = new Intent(ProfileActivity.this, SettingsActivity.class);
            // startActivity(intent);
        });

        btnEditAvatar.setOnClickListener(v -> {
            // Open image picker for avatar
            openImagePicker();
        });

        btnEditProfile.setOnClickListener(v -> {
            // Navigate to edit profile
            Toast.makeText(this, "Edit Profile feature coming soon!", Toast.LENGTH_SHORT).show();
            // Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
            // startActivity(intent);
        });

        btnManageAddress.setOnClickListener(v -> {
            // Navigate to address management
            Toast.makeText(this, "Address Management feature coming soon!", Toast.LENGTH_SHORT).show();
            // Intent intent = new Intent(ProfileActivity.this, AddressManagementActivity.class);
            // startActivity(intent);
        });

        btnAddAddress.setOnClickListener(v -> {
            // Navigate to add new address
            Toast.makeText(this, "Add Address feature coming soon!", Toast.LENGTH_SHORT).show();
            // Intent intent = new Intent(ProfileActivity.this, AddAddressActivity.class);
            // startActivity(intent);
        });

        layoutMyOrders.setOnClickListener(v -> {
            // Navigate to orders
            Toast.makeText(this, "My Orders feature coming soon!", Toast.LENGTH_SHORT).show();
            // Intent intent = new Intent(ProfileActivity.this, OrdersActivity.class);
            // startActivity(intent);
        });

        layoutWishlist.setOnClickListener(v -> {
            // Navigate to wishlist
            if (FavoritesActivity.class != null) {
                Intent intent = new Intent(ProfileActivity.this, FavoritesActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Wishlist feature coming soon!", Toast.LENGTH_SHORT).show();
            }
        });

        layoutNotifications.setOnClickListener(v -> {
            // Navigate to notifications settings
            Toast.makeText(this, "Notifications feature coming soon!", Toast.LENGTH_SHORT).show();
            // Intent intent = new Intent(ProfileActivity.this, NotificationsActivity.class);
            // startActivity(intent);
        });

        layoutHelpSupport.setOnClickListener(v -> {
            // Navigate to help & support
            Toast.makeText(this, "Help & Support feature coming soon!", Toast.LENGTH_SHORT).show();
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
        try {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, 100);
        } catch (Exception e) {
            Toast.makeText(this, "Unable to open image picker", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            try {
                // Set the selected image to avatar
                imgAvatar.setImageURI(data.getData());
                Toast.makeText(this, "Avatar updated successfully!", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(this, "Failed to update avatar", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setupBottomNavigation() {
        bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                // Navigate to home
                Intent intent = new Intent(ProfileActivity.this, HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.nav_cart) {
                // Navigate to cart
                if (CartActivity.class != null) {
                    Intent intent = new Intent(ProfileActivity.this, CartActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(this, "Cart feature coming soon!", Toast.LENGTH_SHORT).show();
                }
                return true;
            } else if (itemId == R.id.nav_favorite) {
                // Navigate to favorites
                if (FavoritesActivity.class != null) {
                    Intent intent = new Intent(ProfileActivity.this, FavoritesActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(this, "Favorites feature coming soon!", Toast.LENGTH_SHORT).show();
                }
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
        // For now, just show a toast and navigate to home
        Toast.makeText(this, "Logged out successfully!", Toast.LENGTH_SHORT).show();

        // Navigate to home screen and clear the activity stack
        Intent intent = new Intent(ProfileActivity.this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();

        // TODO: Implement proper logout logic
        // Clear SharedPreferences
        // Navigate to login screen if you have one
        // Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
        // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        // startActivity(intent);
        // finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Set the correct bottom navigation item when returning to this activity
        bottomNavigation.setSelectedItemId(R.id.nav_profile);
    }
}
