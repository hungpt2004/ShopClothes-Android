package com.example.shopclothes_android;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout tilEmail, tilPassword;
    private TextInputEditText etEmail, etPassword;
    private MaterialButton btnLogin, btnGoogleSignin;
    private TextView tvForgotPassword, tvSignup;
    private LinearLayout llSignup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initViews();
        setupClickListeners();
    }

    private void initViews() {
        tilEmail = findViewById(R.id.til_email);
        tilPassword = findViewById(R.id.til_password);
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        btnGoogleSignin = findViewById(R.id.btn_google_signin);
        tvForgotPassword = findViewById(R.id.tv_forgot_password);
        tvSignup = findViewById(R.id.tv_signup);
        llSignup = findViewById(R.id.ll_signup);
    }

    private void setupClickListeners() {
        btnLogin.setOnClickListener(v -> performLogin());
        
        btnGoogleSignin.setOnClickListener(v -> {
            Toast.makeText(this, "Google Sign-in coming soon!", Toast.LENGTH_SHORT).show();
        });
        
        tvForgotPassword.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
        });
        
        llSignup.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    private void performLogin() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Clear previous errors
        tilEmail.setError(null);
        tilPassword.setError(null);

        boolean isValid = true;

        // Validate email
        if (TextUtils.isEmpty(email)) {
            tilEmail.setError(getString(R.string.error_email_empty));
            isValid = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError(getString(R.string.error_email_invalid));
            isValid = false;
        }

        // Validate password
        if (TextUtils.isEmpty(password)) {
            tilPassword.setError(getString(R.string.error_password_empty));
            isValid = false;
        } else if (password.length() < 6) {
            tilPassword.setError(getString(R.string.error_password_short));
            isValid = false;
        }

        if (isValid) {
            btnLogin.setText(getString(R.string.loading));
            btnLogin.setEnabled(false);

            btnLogin.postDelayed(() -> {
                ProfileManager profileManager = ProfileManager.getInstance();
                profileManager.initialize(getApplicationContext());
                User user = profileManager.getCurrentUser();
                // Nếu đúng tài khoản mẫu hoặc đúng tài khoản đã đăng ký
                boolean isDefault = email.equals("nguyenvanan@gmail.com") && password.equals("12345678");
                boolean isRegistered = user != null && email.equals(user.getEmail()) && !user.getEmail().isEmpty();
                if ((isDefault && password.equals("12345678")) || (isRegistered && password.equals("12345678"))) {
                    if (isDefault) {
                        // Lưu user mẫu vào ProfileManager
                        User defaultUser = new User();
                        defaultUser.setName("Nguyễn Văn An");
                        defaultUser.setEmail("nguyenvanan@gmail.com");
                        defaultUser.setPhone("0123 456 789");
                        defaultUser.setBirthDate("15/03/1995");
                        defaultUser.setGender("Nam");
                        defaultUser.setAvatarPath("");
                        profileManager.saveUser(defaultUser);
                    }
                    Toast.makeText(this, getString(R.string.success_login), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                } else if (isRegistered && password.equals("12345678")) {
                    // Đăng nhập user đã đăng ký
                    Toast.makeText(this, getString(R.string.success_login), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    btnLogin.setText(getString(R.string.login));
                    btnLogin.setEnabled(true);
                    Toast.makeText(this, getString(R.string.error_login_failed), Toast.LENGTH_SHORT).show();
                }
            }, 1000);
        }
    }
}

