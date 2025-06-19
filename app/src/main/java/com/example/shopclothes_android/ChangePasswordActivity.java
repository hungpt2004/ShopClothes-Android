package com.example.shopclothes_android;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

public class ChangePasswordActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());
        TextInputLayout tilPassword = findViewById(R.id.til_password);
        TextInputEditText etPassword = findViewById(R.id.et_password);
        MaterialButton btnUpdate = findViewById(R.id.btn_update_password);
        btnUpdate.setOnClickListener(v -> {
            String newPassword = etPassword.getText().toString().trim();
            tilPassword.setError(null);
            if (TextUtils.isEmpty(newPassword) || newPassword.length() < 6) {
                tilPassword.setError("Password must be at least 6 characters");
                return;
            }
            FirebaseAuth auth = FirebaseAuth.getInstance();
            if (auth.getCurrentUser() != null) {
                btnUpdate.setText("Updating...");
                btnUpdate.setEnabled(false);
                auth.getCurrentUser().updatePassword(newPassword)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(this, "Password updated!", Toast.LENGTH_SHORT).show();
                            btnUpdate.setText("Update Password");
                            btnUpdate.setEnabled(true);
                            finish();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(this, "Failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            btnUpdate.setText("Update Password");
                            btnUpdate.setEnabled(true);
                        });
            }
        });
    }
}