package com.example.shopclothes_android;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class AdminDashboardActivity extends AppCompatActivity {
    private LinearLayout usersContainer;
    private Button btnLogout;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ScrollView scrollView = new ScrollView(this);
        LinearLayout rootLayout = new LinearLayout(this);
        rootLayout.setOrientation(LinearLayout.VERTICAL);
        usersContainer = new LinearLayout(this);
        usersContainer.setOrientation(LinearLayout.VERTICAL);
        btnLogout = new Button(this);
        btnLogout.setText("Logout");
        btnLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(AdminDashboardActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
        TextView title = new TextView(this);
        title.setText("All Users");
        title.setTextSize(24);
        title.setPadding(40, 80, 40, 40);
        rootLayout.addView(title);
        rootLayout.addView(usersContainer);
        rootLayout.addView(btnLogout);
        scrollView.addView(rootLayout);
        setContentView(scrollView);
        db = FirebaseFirestore.getInstance();
        loadUsers();
    }

    private void loadUsers() {
        usersContainer.removeAllViews();
        db.collection("users").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String name = document.getString("name");
                    String email = document.getString("email");
                    String phone = document.getString("phone");
                    String gender = document.getString("gender");
                    String birthDate = document.getString("birthDate");
                    String userInfo = "Name: " + name + "\nEmail: " + email + "\nPhone: " + phone + "\nGender: "
                            + gender + "\nBirth Date: " + birthDate;
                    TextView tv = new TextView(this);
                    tv.setText(userInfo);
                    tv.setPadding(40, 20, 40, 20);
                    usersContainer.addView(tv);
                }
            } else {
                TextView errorTv = new TextView(this);
                errorTv.setText("Failed to load users.");
                usersContainer.addView(errorTv);
            }
        });
    }
}