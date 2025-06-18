package com.example.shopclothes_android;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;

public class LoginActivity extends AppCompatActivity {

			private MaterialButton btnLogin; // Khai báo biến nút login

			@Override
			protected void onCreate(Bundle savedInstanceState) {
						super.onCreate(savedInstanceState);
						setContentView(R.layout.activity_login); // gắn layout login

						// Ánh xạ nút đăng nhập từ XML
						btnLogin = findViewById(R.id.btn_login);

						// Gắn sự kiện khi người dùng bấm nút
						btnLogin.setOnClickListener(new View.OnClickListener() {
									@Override
									public void onClick(View view) {

												// 👉 Khi bấm, chuyển qua HomeActivity
												Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
												startActivity(intent);

												// ✅ Không cho quay lại LoginActivity nữa
												finish();
									}
						});
			}
}

