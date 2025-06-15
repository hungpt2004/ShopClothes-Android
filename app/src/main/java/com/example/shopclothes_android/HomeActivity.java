package com.example.shopclothes_android;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.Arrays;
import java.util.List;

public class HomeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        RecyclerView rvProducts = findViewById(R.id.rvProducts);
        rvProducts.setLayoutManager(new LinearLayoutManager(this));

        // Sample data
        List<String> products = Arrays.asList("T-shirt", "Jeans", "Jacket", "Dress", "Shorts");
        ProductAdapter adapter = new ProductAdapter(products);
        rvProducts.setAdapter(adapter);

        // Avatar click
        ImageView imgAvatar = findViewById(R.id.imgAvatar);
        imgAvatar.setOnClickListener(v -> {
            // TODO: Chuyển tới ProfileActivity (chưa tạo)
            Toast.makeText(HomeActivity.this, "Đi tới trang cá nhân (chưa tạo)", Toast.LENGTH_SHORT).show();
        });
        // Cart click
        ImageView imgCart = findViewById(R.id.imgCart);
        imgCart.setOnClickListener(v -> {
            startActivity(new Intent(HomeActivity.this, CartActivity.class));
        });
    }
}
