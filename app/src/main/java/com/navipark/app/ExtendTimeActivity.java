package com.navipark.app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class ExtendTimeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_extend_time);
        
        Button btnExtend1Hour = findViewById(R.id.btnExtend1Hour);
        Button btnNoThanks = findViewById(R.id.btnNoThanks);
        
        btnExtend1Hour.setOnClickListener(v -> {
            Intent intent = new Intent(ExtendTimeActivity.this, PaymentActivity.class);
            intent.putExtra("totalPrice", 25);
            startActivity(intent);
            finish();
        });
        
        btnNoThanks.setOnClickListener(v -> {
            Toast.makeText(this, "Please vacate the spot soon.", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}
