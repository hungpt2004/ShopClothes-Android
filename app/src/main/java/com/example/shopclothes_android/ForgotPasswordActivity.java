package com.example.shopclothes_android;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class ForgotPasswordActivity extends AppCompatActivity {

    private MaterialToolbar toolbar;
    private TextInputLayout tilEmail;
    private TextInputEditText etEmail;
    private MaterialButton btnSendResetLink, btnResendLink;
    private TextView tvBackToLogin, tvResetLinkSent, tvCheckEmail;
    private boolean isLinkSent = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        initViews();
        setupToolbar();
        setupClickListeners();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        tilEmail = findViewById(R.id.til_email);
        etEmail = findViewById(R.id.et_email);
        btnSendResetLink = findViewById(R.id.btn_send_reset_link);
        btnResendLink = findViewById(R.id.btn_resend_link);
        tvBackToLogin = findViewById(R.id.tv_back_to_login);
        tvResetLinkSent = findViewById(R.id.tv_reset_link_sent);
        tvCheckEmail = findViewById(R.id.tv_check_email);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupClickListeners() {
        btnSendResetLink.setOnClickListener(v -> sendResetLink());
        
        btnResendLink.setOnClickListener(v -> sendResetLink());
        
        tvBackToLogin.setOnClickListener(v -> {
            Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });
    }

    private void sendResetLink() {
        String email = etEmail.getText().toString().trim();

        // Clear previous errors
        tilEmail.setError(null);

        boolean isValid = true;

        // Validate email
        if (TextUtils.isEmpty(email)) {
            tilEmail.setError(getString(R.string.error_email_empty));
            isValid = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError(getString(R.string.error_email_invalid));
            isValid = false;
        }

        if (isValid) {
            // Simulate sending reset link
            MaterialButton activeButton = isLinkSent ? btnResendLink : btnSendResetLink;
            activeButton.setText(getString(R.string.loading));
            activeButton.setEnabled(false);

            // Simulate network delay
            activeButton.postDelayed(() -> {
                showSuccessState();
                Toast.makeText(this, getString(R.string.success_password_reset), Toast.LENGTH_LONG).show();
                
                activeButton.setText(isLinkSent ? getString(R.string.resend_link) : getString(R.string.send_reset_link));
                activeButton.setEnabled(true);
            }, 2000);
        }
    }

    private void showSuccessState() {
        if (!isLinkSent) {
            isLinkSent = true;
            
            // Hide initial elements
            btnSendResetLink.setVisibility(View.GONE);
            
            // Show success elements
            tvResetLinkSent.setVisibility(View.VISIBLE);
            tvCheckEmail.setVisibility(View.VISIBLE);
            btnResendLink.setVisibility(View.VISIBLE);
            
            // Update email field to read-only
            etEmail.setEnabled(false);
            tilEmail.setEnabled(false);
        }
    }
}
