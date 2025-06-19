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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import android.util.Log;

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout tilEmail, tilPassword;
    private TextInputEditText etEmail, etPassword;
    private MaterialButton btnLogin, btnGoogleSignin;
    private TextView tvForgotPassword, tvSignup;
    private LinearLayout llSignup;
    private FirebaseAuth mAuth;
    private static final String TAG = "LoginActivity";
    private static final String ADMIN_EMAIL = "admin@gmail.com";
    private static final String ADMIN_PASSWORD = "admin123";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
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

            // Check for admin login
            if (email.equals(ADMIN_EMAIL) && password.equals(ADMIN_PASSWORD)) {
                Toast.makeText(this, "Admin login successful!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(LoginActivity.this, AdminDashboardActivity.class);
                startActivity(intent);
                finish();
                return;
            }

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser firebaseUser = mAuth.getCurrentUser();
                            // Load user data from ProfileManager
                            ProfileManager profileManager = ProfileManager.getInstance();
                            profileManager.initialize(getApplicationContext());
                            User user = profileManager.getCurrentUser();

                            // If user doesn't exist in ProfileManager, create default user
                            if (user == null || user.getEmail().isEmpty()) {
                                user = new User();
                                user.setName("User");
                                user.setEmail(email);
                                user.setPhone("");
                                user.setBirthDate("01/01/2000");
                                user.setGender("Other");
                                user.setAvatarPath("https://ui-avatars.com/api/?name=User");
                                profileManager.saveUser(user);
                            }

                            Toast.makeText(this, getString(R.string.success_login), Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            btnLogin.setText(getString(R.string.login));
                            btnLogin.setEnabled(true);
                            Exception e = task.getException();
                            String errorMsg = getString(R.string.error_login_failed);
                            if (e != null) {
                                errorMsg += "\n" + e.getMessage();
                                Log.e(TAG, "Firebase login failed", e);
                            }
                            Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show();
                        }
                    });
        }
    }
}
