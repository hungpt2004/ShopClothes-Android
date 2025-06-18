package com.example.shopclothes_android;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class PaymentSuccessActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_success);

        TextView successMsg = findViewById(R.id.success_msg);
        TextView txDetails = findViewById(R.id.tv_transaction_details);
        Button btnBack = findViewById(R.id.btn_back_home);

        // Get total amount from intent
        String totalAmount = getIntent().getStringExtra("total_amount");
        if (totalAmount == null) {
            totalAmount = "$0.00";
        }

        // Set transaction info
        successMsg.setText("Thanh toán thành công!");
        txDetails.setText("Số tiền thanh toán: " + totalAmount + "\nCảm ơn bạn đã mua hàng!");

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate back to main activity
                Intent intent = new Intent(PaymentSuccessActivity.this, HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });
    }
}
