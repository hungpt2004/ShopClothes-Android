package com.example.shopclothes_android;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;

public class CartActivity extends AppCompatActivity {

    private ListView lvCart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        lvCart = findViewById(R.id.lvCart);

        // Lấy danh sách sản phẩm từ CartManager
        List<String> cartItems = CartManager.getInstance().getCartItems();

        // Adapter hiển thị danh sách sản phẩm trong giỏ
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                cartItems
        );

        lvCart.setAdapter(adapter);

        // Thêm sự kiện click nút back để trở về
        findViewById(R.id.back_button).setOnClickListener(v -> {
            finish(); // Kết thúc Activity hiện tại
        });
    }
}
