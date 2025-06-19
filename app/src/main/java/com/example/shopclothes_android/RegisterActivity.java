package com.example.shopclothes_android;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegisterActivity extends AppCompatActivity {

    private MaterialToolbar toolbar;
    private TextInputLayout tilFullName, tilEmail, tilPhone, tilPassword, tilConfirmPassword;
    private TextInputEditText etFullName, etEmail, etPhone, etPassword, etConfirmPassword;
    private CheckBox cbTerms;
    private MaterialButton btnCreateAccount, btnGoogleSignup;
    private LinearLayout llSignin;
    private TextView tvSignin, tvTermsService, tvPrivacyPolicy;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private static final String TAG = "RegisterActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        initViews();
        setupToolbar();
        setupClickListeners();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        tilFullName = findViewById(R.id.til_full_name);
        tilEmail = findViewById(R.id.til_email);
        tilPhone = findViewById(R.id.til_phone);
        tilPassword = findViewById(R.id.til_password);
        tilConfirmPassword = findViewById(R.id.til_confirm_password);
        etFullName = findViewById(R.id.et_full_name);
        etEmail = findViewById(R.id.et_email);
        etPhone = findViewById(R.id.et_phone);
        etPassword = findViewById(R.id.et_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);
        cbTerms = findViewById(R.id.cb_terms);
        btnCreateAccount = findViewById(R.id.btn_create_account);
        btnGoogleSignup = findViewById(R.id.btn_google_signup);
        llSignin = findViewById(R.id.ll_signin);
        tvSignin = findViewById(R.id.tv_signin);
        tvTermsService = findViewById(R.id.tv_terms_service);
        tvPrivacyPolicy = findViewById(R.id.tv_privacy_policy);
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
        btnCreateAccount.setOnClickListener(v -> performRegistration());

        btnGoogleSignup.setOnClickListener(v -> {
            Toast.makeText(this, "Google Sign-up coming soon!", Toast.LENGTH_SHORT).show();
        });

        llSignin.setOnClickListener(v -> finish());

        tvTermsService.setOnClickListener(v -> {
            Toast.makeText(this, "Terms of Service", Toast.LENGTH_SHORT).show();
        });

        tvPrivacyPolicy.setOnClickListener(v -> {
            Toast.makeText(this, "Privacy Policy", Toast.LENGTH_SHORT).show();
        });
    }

    private void performRegistration() {
        String fullName = etFullName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        // Clear previous errors
        clearErrors();

        boolean isValid = validateInputs(fullName, email, phone, password, confirmPassword);

        if (isValid) {
            btnCreateAccount.setText(getString(R.string.loading));
            btnCreateAccount.setEnabled(false);

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser firebaseUser = mAuth.getCurrentUser();
                            // Set default values
                            String defaultAvatarUrl = "https://ui-avatars.com/api/?name=" + fullName.replace(" ", "+");
                            String defaultGender = "Other";
                            String defaultBirthDate = "01/01/2000";

                            // Create user data map for Firestore
                            java.util.Map<String, Object> userData = new java.util.HashMap<>();
                            userData.put("name", fullName);
                            userData.put("email", email);
                            userData.put("phone", phone);
                            userData.put("birthDate", defaultBirthDate);
                            userData.put("gender", defaultGender);
                            userData.put("avatarUrl", defaultAvatarUrl);
                            userData.put("createdAt", new java.util.Date());
                            userData.put("isBanned", false);

                            // Save to Firestore
                            db.collection("users").document(firebaseUser.getUid())
                                    .set(userData)
                                    .addOnSuccessListener(aVoid -> {
                                        Log.d(TAG, "User data saved to Firestore");
                                        // Save user info locally
                                        ProfileManager profileManager = ProfileManager.getInstance();
                                        profileManager.initialize(getApplicationContext());
                                        User user = new User();
                                        user.setName(fullName);
                                        user.setEmail(email);
                                        user.setPhone(phone);
                                        user.setBirthDate(defaultBirthDate);
                                        user.setGender(defaultGender);
                                        user.setAvatarPath(defaultAvatarUrl);
                                        profileManager.saveUser(user);
                                        Toast.makeText(this, getString(R.string.success_registration),
                                                Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        finish();
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e(TAG, "Error saving user data to Firestore", e);
                                        Toast.makeText(this, "Account created but failed to save profile data",
                                                Toast.LENGTH_LONG).show();
                                        // Still proceed with local save
                                        ProfileManager profileManager = ProfileManager.getInstance();
                                        profileManager.initialize(getApplicationContext());
                                        User user = new User();
                                        user.setName(fullName);
                                        user.setEmail(email);
                                        user.setPhone(phone);
                                        user.setBirthDate(defaultBirthDate);
                                        user.setGender(defaultGender);
                                        user.setAvatarPath(defaultAvatarUrl);
                                        profileManager.saveUser(user);
                                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        finish();
                                    });
                        } else {
                            btnCreateAccount.setText(getString(R.string.create_account_button));
                            btnCreateAccount.setEnabled(true);
                            Exception e = task.getException();
                            String errorMsg = getString(R.string.error_registration_failed);
                            if (e != null) {
                                if (e instanceof com.google.firebase.auth.FirebaseAuthUserCollisionException) {
                                    errorMsg = getString(R.string.error_email_exists);
                                    tilEmail.setError(errorMsg);
                                } else {
                                    errorMsg += "\n" + e.getMessage();
                                }
                                Log.e(TAG, "Firebase registration failed", e);
                            }
                            Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show();
                        }
                    });
        }
    }

    private void clearErrors() {
        tilFullName.setError(null);
        tilEmail.setError(null);
        tilPhone.setError(null);
        tilPassword.setError(null);
        tilConfirmPassword.setError(null);
    }

    private boolean validateInputs(String fullName, String email, String phone, String password,
            String confirmPassword) {
        boolean isValid = true;

        // Validate full name
        if (TextUtils.isEmpty(fullName)) {
            tilFullName.setError(getString(R.string.error_name_empty));
            isValid = false;
        }

        // Validate email
        if (TextUtils.isEmpty(email)) {
            tilEmail.setError(getString(R.string.error_email_empty));
            isValid = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError(getString(R.string.error_email_invalid));
            isValid = false;
        }

        // Validate phone
        if (TextUtils.isEmpty(phone)) {
            tilPhone.setError(getString(R.string.error_phone_empty));
            isValid = false;
        } else if (phone.length() < 10) {
            tilPhone.setError(getString(R.string.error_phone_invalid));
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

        // Validate confirm password
        if (TextUtils.isEmpty(confirmPassword)) {
            tilConfirmPassword.setError(getString(R.string.error_password_empty));
            isValid = false;
        } else if (!password.equals(confirmPassword)) {
            tilConfirmPassword.setError(getString(R.string.error_password_mismatch));
            isValid = false;
        }

        // Validate terms acceptance
        if (!cbTerms.isChecked()) {
            Toast.makeText(this, getString(R.string.error_terms_not_accepted), Toast.LENGTH_SHORT).show();
            isValid = false;
        }

        return isValid;
    }
}
