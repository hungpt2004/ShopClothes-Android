package com.example.shopclothes_android;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AdminDashboardActivity extends AppCompatActivity implements UserAdapter.UserClickListener {
    private RecyclerView rvUsers;
    private UserAdapter userAdapter;
    private List<User> userList = new ArrayList<>();
    private List<User> allUsers = new ArrayList<>();
    private FirebaseFirestore db;
    private MaterialToolbar toolbar;
    private BottomNavigationView bottomNav;
    private EditText etSearch;
    private ImageButton btnFilter;
    private View progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        rvUsers = findViewById(R.id.rv_users);
        rvUsers.setLayoutManager(new LinearLayoutManager(this));
        userAdapter = new UserAdapter(userList, this);
        rvUsers.setAdapter(userAdapter);
        db = FirebaseFirestore.getInstance();
        bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setSelectedItemId(R.id.nav_admin);
        bottomNav.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_admin)
                return true;
            if (item.getItemId() == R.id.nav_settings) {
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            }
            if (item.getItemId() == R.id.nav_product_dashboard) {
                startActivity(new Intent(this, ProductDashboardActivity.class));
                return true;
            }
            return false;
        });
        etSearch = findViewById(R.id.et_search);
        btnFilter = findViewById(R.id.btn_filter);
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterUsers(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        btnFilter.setOnClickListener(v -> showFilterDialog());
        progressBar = findViewById(R.id.progress_bar);
        loadUsers();
    }

    private void loadUsers() {
        progressBar.setVisibility(View.VISIBLE);
        rvUsers.setAlpha(0.5f);
        userList.clear();
        allUsers.clear();
        db.collection("users").get().addOnCompleteListener(task -> {
            progressBar.setVisibility(View.GONE);
            rvUsers.setAlpha(1f);
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot doc : task.getResult()) {
                    String name = doc.getString("name");
                    String email = doc.getString("email");
                    String phone = doc.getString("phone");
                    String gender = doc.getString("gender");
                    String birthDate = doc.getString("birthDate");
                    String avatar = doc.getString("avatarUrl");
                    Boolean isBanned = doc.getBoolean("isBanned");
                    User user = new User();
                    user.setName(name);
                    user.setEmail(email);
                    user.setPhone(phone);
                    user.setGender(gender);
                    user.setBirthDate(birthDate);
                    user.setAvatarPath(avatar);
                    user.isBanned = isBanned != null && isBanned;
                    allUsers.add(user);
                }
                userList.addAll(allUsers);
                userAdapter.notifyDataSetChanged();
            } else {
                Toast.makeText(this, "Failed to load users.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filterUsers(String query) {
        userList.clear();
        if (query.isEmpty()) {
            userList.addAll(allUsers);
        } else {
            for (User user : allUsers) {
                if (user.getName().toLowerCase(Locale.ROOT).contains(query.toLowerCase(Locale.ROOT)) ||
                        user.getEmail().toLowerCase(Locale.ROOT).contains(query.toLowerCase(Locale.ROOT)) ||
                        user.getPhone().toLowerCase(Locale.ROOT).contains(query.toLowerCase(Locale.ROOT))) {
                    userList.add(user);
                }
            }
        }
        userAdapter.notifyDataSetChanged();
    }

    private void showFilterDialog() {
        // TODO: Implement filter dialog (e.g., by status, gender)
        Toast.makeText(this, "Filter feature coming soon!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUserClicked(User user) {
        Intent intent = new Intent(this, UserDetailActivity.class);
        intent.putExtra("user_name", user.getName());
        intent.putExtra("user_email", user.getEmail());
        intent.putExtra("user_phone", user.getPhone());
        intent.putExtra("user_gender", user.getGender());
        intent.putExtra("user_birthDate", user.getBirthDate());
        intent.putExtra("user_avatar", user.getAvatarPath());
        intent.putExtra("user_id", user.getEmail());
        intent.putExtra("user_banned", user.isBanned != null && user.isBanned);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.admin_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCallClicked(User user) {
        if (user.getPhone() != null && !user.getPhone().isEmpty()) {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(android.net.Uri.parse("tel:" + user.getPhone()));
            startActivity(intent);
        } else {
            Toast.makeText(this, "No phone number available", Toast.LENGTH_SHORT).show();
        }
    }
}