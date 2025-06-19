package com.example.shopclothes_android;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.google.android.material.appbar.MaterialToolbar;
import de.hdodenhof.circleimageview.CircleImageView;
import android.widget.Button;
import android.widget.ImageButton;
import android.content.Intent;
import android.net.Uri;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class UserDetailActivity extends AppCompatActivity {
    private boolean isBanned = false;
    private String userEmail;
    private String userPhone;
    private String userId;
    private Button btnBan;
    private ImageButton btnCall;
    private FirebaseFirestore db;
    private FloatingActionButton floatingCall;
    private BottomNavigationView bottomNav;
    private TextView tvStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> {
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });
        CircleImageView avatar = findViewById(R.id.img_avatar);
        TextView tvName = findViewById(R.id.tv_name);
        TextView tvEmail = findViewById(R.id.tv_email);
        TextView tvPhone = findViewById(R.id.tv_phone);
        TextView tvGender = findViewById(R.id.tv_gender);
        TextView tvBirthDate = findViewById(R.id.tv_birth_date);
        btnBan = findViewById(R.id.btn_ban);
        floatingCall = findViewById(R.id.btn_call);
        bottomNav = findViewById(R.id.bottom_navigation);
        tvStatus = findViewById(R.id.tv_status);
        db = FirebaseFirestore.getInstance();
        try {
            String name = getIntent().getStringExtra("user_name");
            userEmail = getIntent().getStringExtra("user_email");
            userPhone = getIntent().getStringExtra("user_phone");
            String gender = getIntent().getStringExtra("user_gender");
            String birthDate = getIntent().getStringExtra("user_birthDate");
            String avatarUrl = getIntent().getStringExtra("user_avatar");
            userId = getIntent().getStringExtra("user_id");
            isBanned = getIntent().getBooleanExtra("user_banned", false);
            tvName.setText(name);
            tvEmail.setText(userEmail);
            tvPhone.setText(userPhone);
            tvGender.setText(gender);
            tvBirthDate.setText(birthDate);
            Glide.with(this)
                    .load(avatarUrl)
                    .placeholder(R.drawable.default_avatar)
                    .error(R.drawable.default_avatar)
                    .into(avatar);
            updateBanButton();
            floatingCall.setOnClickListener(v -> callUser());
            bottomNav.setSelectedItemId(R.id.nav_admin);
            bottomNav.setOnItemSelectedListener(item -> {
                if (item.getItemId() == R.id.nav_admin) {
                    finish();
                    return true;
                }
                if (item.getItemId() == R.id.nav_settings) {
                    startActivity(new Intent(this, SettingsActivity.class));
                    return true;
                }
                return false;
            });
        } catch (Exception e) {
            Toast.makeText(this, "Error loading user details", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateBanButton() {
        btnBan.setText(isBanned ? "Unban" : "Ban");
        btnBan.setBackgroundColor(getResources().getColor(isBanned ? R.color.primary_color : R.color.error_color));
        tvStatus.setText(isBanned ? "Banned" : "Active");
        tvStatus.setBackgroundResource(isBanned ? R.drawable.bg_chip_banned : R.drawable.bg_chip_active);
    }

    private void toggleBanStatus() {
        if (userId == null || userId.isEmpty()) {
            Toast.makeText(this, "User ID not found", Toast.LENGTH_SHORT).show();
            return;
        }
        db.collection("users").document(userId)
                .update("isBanned", !isBanned)
                .addOnSuccessListener(aVoid -> {
                    isBanned = !isBanned;
                    updateBanButton();
                    Toast.makeText(this, isBanned ? "User banned" : "User unbanned", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to update status", Toast.LENGTH_SHORT).show());
    }

    private void callUser() {
        if (userPhone != null && !userPhone.isEmpty()) {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + userPhone));
            startActivity(intent);
        } else {
            Toast.makeText(this, "No phone number available", Toast.LENGTH_SHORT).show();
        }
    }
}