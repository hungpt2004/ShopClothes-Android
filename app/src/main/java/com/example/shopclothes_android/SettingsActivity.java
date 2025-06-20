package com.example.shopclothes_android;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class SettingsActivity extends AppCompatActivity {
    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());
        MaterialButton btnLogout = findViewById(R.id.btn_logout);
        btnLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
        bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setSelectedItemId(R.id.nav_settings);
        bottomNav.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_admin) {
                startActivity(new Intent(this, AdminDashboardActivity.class));
                finish();
                return true;
            }
            if (item.getItemId() == R.id.nav_product_dashboard) {
                startActivity(new Intent(this, ProductDashboardActivity.class));
                finish();
                return true;
            }
            if (item.getItemId() == R.id.nav_settings)
                return true;
            return false;
        });
    }
}