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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.GoogleAuthProvider;
import java.util.HashMap;
import java.util.Map;
import com.google.android.gms.common.SignInButton;

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout tilEmail, tilPassword;
    private TextInputEditText etEmail, etPassword;
    private MaterialButton btnLogin;
    private SignInButton btnGoogleSignin;
    private TextView tvForgotPassword, tvSignup;
    private LinearLayout llSignup;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private static final String TAG = "LoginActivity";
    private static final String ADMIN_EMAIL = "admin@gmail.com";
    private static final String ADMIN_PASSWORD = "admin123";
    private static final int RC_SIGN_IN = 9001;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        initViews();
        setupClickListeners();

        // Google Sign-In options
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
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

        btnGoogleSignin.setOnClickListener(v -> signInWithGoogle());

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
                            if (firebaseUser != null) {
                                db.collection("users").document(firebaseUser.getUid()).get()
                                        .addOnSuccessListener(document -> {
                                            Boolean isBanned = document.getBoolean("isBanned");
                                            if (isBanned != null && isBanned) {
                                                FirebaseAuth.getInstance().signOut();
                                                btnLogin.setText(getString(R.string.login));
                                                btnLogin.setEnabled(true);
                                                Toast.makeText(this, "Your account has been banned.", Toast.LENGTH_LONG)
                                                        .show();
                                            } else {
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

                                                Toast.makeText(this, getString(R.string.success_login),
                                                        Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                                startActivity(intent);
                                                finish();
                                            }
                                        })
                                        .addOnFailureListener(e -> {
                                            btnLogin.setText(getString(R.string.login));
                                            btnLogin.setEnabled(true);
                                            Toast.makeText(this, "Failed to check account status.", Toast.LENGTH_LONG)
                                                    .show();
                                        });
                            }
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

    private void signInWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account != null) {
                    firebaseAuthWithGoogle(account.getIdToken());
                } else {
                    Toast.makeText(this, "Google sign in failed", Toast.LENGTH_SHORT).show();
                }
            } catch (ApiException e) {
                Toast.makeText(this, "Google sign in failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            if (task.getResult().getAdditionalUserInfo().isNewUser()) {
                                createNewUserInFirestore(user);
                            } else {
                                fetchAndSaveUser(user.getUid());
                            }
                        }
                    } else {
                        Toast.makeText(this, "Authentication Failed.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void createNewUserInFirestore(FirebaseUser firebaseUser) {
        Map<String, Object> user = new HashMap<>();
        user.put("uid", firebaseUser.getUid());
        user.put("email", firebaseUser.getEmail());
        user.put("name",
                firebaseUser.getDisplayName() != null ? firebaseUser.getDisplayName() : "User");
        user.put("avatarUrl",
                firebaseUser.getPhotoUrl() != null ? firebaseUser.getPhotoUrl().toString() : "");
        user.put("gender", "Not specified");
        user.put("birthDate", "Not specified");
        user.put("isBanned", false);
        db.collection("users").document(firebaseUser.getUid()).set(user)
                .addOnSuccessListener(aVoid -> fetchAndSaveUser(firebaseUser.getUid()))
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to save user info.", Toast.LENGTH_SHORT).show();
                });
    }

    // Fetch user document from Firestore and save to ProfileManager
    private void fetchAndSaveUser(String uid) {
        db.collection("users").document(uid).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        User user = new User();
                        user.setName(documentSnapshot.getString("name"));
                        user.setEmail(documentSnapshot.getString("email"));
                        user.setPhone(
                                documentSnapshot.getString("phone") != null ? documentSnapshot.getString("phone") : "");
                        user.setBirthDate(documentSnapshot.getString("birthDate") != null
                                ? documentSnapshot.getString("birthDate")
                                : "");
                        user.setGender(
                                documentSnapshot.getString("gender") != null ? documentSnapshot.getString("gender")
                                        : "");
                        user.setAvatarPath(documentSnapshot.getString("avatarUrl") != null
                                ? documentSnapshot.getString("avatarUrl")
                                : "");
                        ProfileManager profileManager = ProfileManager.getInstance();
                        profileManager.initialize(getApplicationContext());
                        profileManager.saveUser(user);
                    }
                    goToHome();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to load user profile.", Toast.LENGTH_SHORT).show();
                    goToHome();
                });
    }

    private void goToHome() {
        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }
}
