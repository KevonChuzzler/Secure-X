package com.navipark.app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class PaymentActivity extends AppCompatActivity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        
        TextView tvTotalAmount = findViewById(R.id.tvTotalAmount);
        Button btnPay = findViewById(R.id.btnPay);
        
        int totalPrice = getIntent().getIntExtra("totalPrice", 0);
        tvTotalAmount.setText("Total Amount: R " + totalPrice + ".00");
        
        btnPay.setOnClickListener(v -> {
            Toast.makeText(this, "Mock Payment Successful! Spot Reserved for R" + totalPrice, Toast.LENGTH_LONG).show();
            Intent intent = new Intent(PaymentActivity.this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }
}
