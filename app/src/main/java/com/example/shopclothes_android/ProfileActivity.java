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
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import android.app.Activity;

public class ProfileActivity extends AppCompatActivity {

	private ImageView btnEditAvatar;
	private CircleImageView imgAvatar;
	private TextView tvUserName, tvUserEmail, tvUserPhone, tvUserBirthDate,
			tvUserGender, tvUserAddress, btnManageAddress;
	private Button btnEditProfile, btnAddAddress;
	private LinearLayout layoutMyOrders, layoutWishlist, layoutLogout;
	private BottomNavigationView bottomNavigation;
	private static final int EDIT_PROFILE_REQUEST = 101;

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
		ProfileManager profileManager = ProfileManager.getInstance();
		profileManager.initialize(getApplicationContext());
		User user = profileManager.getCurrentUser();
		if (user != null) {
			tvUserName.setText(user.getName());
			tvUserEmail.setText(user.getEmail());
			tvUserPhone.setText(user.getPhone());
			tvUserBirthDate.setText(user.getBirthDate());
			tvUserGender.setText(user.getGender());
			// Load avatar with Glide
			if (imgAvatar != null && user.getAvatarPath() != null && !user.getAvatarPath().isEmpty()) {
				Glide.with(this)
						.load(user.getAvatarPath())
						.placeholder(R.drawable.default_avatar)
						.error(R.drawable.default_avatar)
						.into(imgAvatar);
			} else if (imgAvatar != null) {
				imgAvatar.setImageResource(R.drawable.default_avatar);
			}
		}
	}

	private void setupClickListeners() {
		if (btnEditAvatar != null)
			btnEditAvatar.setOnClickListener(v -> openImagePicker());
		if (imgAvatar != null)
			imgAvatar.setOnClickListener(v -> openImagePicker());
		if (btnEditProfile != null)
			btnEditProfile.setOnClickListener(
					v -> {
						Intent intent = new Intent(this, EditProfileActivity.class);
						startActivityForResult(intent, EDIT_PROFILE_REQUEST);
					});
		if (btnManageAddress != null)
			btnManageAddress.setOnClickListener(
					v -> Toast.makeText(this, "Address Management feature coming soon!", Toast.LENGTH_SHORT).show());
		if (btnAddAddress != null)
			btnAddAddress.setOnClickListener(
					v -> Toast.makeText(this, "Add Address feature coming soon!", Toast.LENGTH_SHORT).show());
		if (layoutMyOrders != null)
			layoutMyOrders.setOnClickListener(
					v -> {
						Intent intent = new Intent(ProfileActivity.this, PurchaseHistoryActivity.class);
						startActivity(intent);
					});
		if (layoutWishlist != null)
			layoutWishlist.setOnClickListener(v -> {
				if (FavoritesActivity.class != null) {
					Intent intent = new Intent(ProfileActivity.this, FavoritesActivity.class);
					startActivity(intent);
				} else {
					Toast.makeText(this, "Wishlist feature coming soon!", Toast.LENGTH_SHORT).show();
				}
			});
		if (layoutLogout != null)
			layoutLogout.setOnClickListener(v -> showLogoutDialog());
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
		if (requestCode == EDIT_PROFILE_REQUEST && resultCode == Activity.RESULT_OK) {
			// Reload user data after editing
			loadUserData();
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
		androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
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
		// Sign out from Firebase
		FirebaseAuth.getInstance().signOut();
		// Clear local user data
		ProfileManager.getInstance().clearAllData();
		Toast.makeText(this, "Logged out successfully!", Toast.LENGTH_SHORT).show();
		// Navigate to login screen and clear the activity stack
		Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
		startActivity(intent);
		finish();
	}

	@Override
	protected void onResume() {
		super.onResume();
		// Set the correct bottom navigation item when returning to this activity
		bottomNavigation.setSelectedItemId(R.id.nav_profile);
	}
}
