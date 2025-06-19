package com.example.shopclothes_android;

import android.content.Context;
import android.content.SharedPreferences;

public class ProfileManager {
			private static ProfileManager instance;
			private SharedPreferences sharedPreferences;
			private User currentUser;

			private static final String PREF_NAME = "profile_prefs";
			private static final String KEY_NAME = "user_name";
			private static final String KEY_EMAIL = "user_email";
			private static final String KEY_PHONE = "user_phone";
			private static final String KEY_BIRTH_DATE = "user_birth_date";
			private static final String KEY_GENDER = "user_gender";
			private static final String KEY_ADDRESS = "user_address";
			private static final String KEY_AVATAR_URI = "user_avatar_uri";
			private static final String KEY_IS_LOGGED_IN = "is_logged_in";
			private static final String KEY_ORDERS_COUNT = "orders_count";
			private static final String KEY_REVIEWS_COUNT = "reviews_count";

			private ProfileManager() {
						// Private constructor
			}

			public static synchronized ProfileManager getInstance() {
						if (instance == null) {
									instance = new ProfileManager();
						}
						return instance;
			}

			public void initialize(Context context) {
						if (sharedPreferences == null) {
									sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
									loadUserFromPreferences();
						}
			}

			private void loadUserFromPreferences() {
						if (currentUser == null) {
									currentUser = new User();
						}

						// Load with default values matching your sample data
						currentUser.setName(sharedPreferences.getString(KEY_NAME, "Nguyễn Văn An"));
						currentUser.setEmail(sharedPreferences.getString(KEY_EMAIL, "nguyenvanan@gmail.com"));
						currentUser.setPhone(sharedPreferences.getString(KEY_PHONE, "0123 456 789"));
						currentUser.setBirthDate(sharedPreferences.getString(KEY_BIRTH_DATE, "15/03/1995"));
						currentUser.setGender(sharedPreferences.getString(KEY_GENDER, "Nam"));
						currentUser.setAvatarPath(sharedPreferences.getString(KEY_AVATAR_URI, ""));
			}

			public void saveUser(User user) {
						this.currentUser = user;
						SharedPreferences.Editor editor = sharedPreferences.edit();
						editor.putString(KEY_NAME, user.getName());
						editor.putString(KEY_EMAIL, user.getEmail());
						editor.putString(KEY_PHONE, user.getPhone());
						editor.putString(KEY_BIRTH_DATE, user.getBirthDate());
						editor.putString(KEY_GENDER, user.getGender());
						editor.putString(KEY_AVATAR_URI, user.getAvatarPath());
						editor.putBoolean(KEY_IS_LOGGED_IN, true);
						editor.apply();
			}

			public User getCurrentUser() {
						if (currentUser == null) {
									loadUserFromPreferences();
						}
						return currentUser;
			}

			// Quick update methods for individual fields
			public void updateUserName(String name) {
						getCurrentUser().setName(name);
						sharedPreferences.edit().putString(KEY_NAME, name).apply();
			}

			public void updateUserEmail(String email) {
						getCurrentUser().setEmail(email);
						sharedPreferences.edit().putString(KEY_EMAIL, email).apply();
			}

			public void updateUserPhone(String phone) {
						getCurrentUser().setPhone(phone);
						sharedPreferences.edit().putString(KEY_PHONE, phone).apply();
			}

			public void updateUserBirthDate(String birthDate) {
						getCurrentUser().setBirthDate(birthDate);
						sharedPreferences.edit().putString(KEY_BIRTH_DATE, birthDate).apply();
			}

			public void updateUserGender(String gender) {
						getCurrentUser().setGender(gender);
						sharedPreferences.edit().putString(KEY_GENDER, gender).apply();
			}



			public void updateUserAvatar(String avatarUri) {
						getCurrentUser().setAvatarPath(avatarUri);
						sharedPreferences.edit().putString(KEY_AVATAR_URI, avatarUri).apply();
			}

			// Stats management
			public void updateOrdersCount(int count) {
						sharedPreferences.edit().putInt(KEY_ORDERS_COUNT, count).apply();
			}

			public void updateReviewsCount(int count) {
						sharedPreferences.edit().putInt(KEY_REVIEWS_COUNT, count).apply();
			}

			public int getOrdersCount() {
						return sharedPreferences.getInt(KEY_ORDERS_COUNT, 12);
			}

			public int getWishlistCount() {
						// Get count from FavoriteManager
						try {
									return FavoriteManager.getInstance().getFavoriteProducts().size();
						} catch (Exception e) {
									return 8; // Default value
						}
			}

			public int getReviewsCount() {
						return sharedPreferences.getInt(KEY_REVIEWS_COUNT, 5);
			}

			// Session management
			public boolean isLoggedIn() {
						return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, true);
			}

			public void logout() {
						SharedPreferences.Editor editor = sharedPreferences.edit();
						editor.putBoolean(KEY_IS_LOGGED_IN, false);
						editor.apply();

						// Don't clear user data, just mark as logged out
						// This allows user to log back in with same data
			}

			public void clearAllData() {
						SharedPreferences.Editor editor = sharedPreferences.edit();
						editor.clear();
						editor.apply();
						currentUser = null;
			}

			// Utility methods
			public String getUserDisplayName() {
						return getCurrentUser().getName();
			}

			public String getUserEmail() {
						return getCurrentUser().getEmail();
			}

			public String getUserAvatarUri() {
						return getCurrentUser().getAvatarPath();
			}

			public boolean hasUserData() {
						return currentUser != null && !getCurrentUser().getName().isEmpty();
			}
}
