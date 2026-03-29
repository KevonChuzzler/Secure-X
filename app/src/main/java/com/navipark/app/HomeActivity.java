package com.navipark.app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        
        Button btnBay1 = findViewById(R.id.btnBay1); 
        Button btnBay2 = findViewById(R.id.btnBay2); 
        
        btnBay1.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, BookingActivity.class);
            intent.putExtra("selected_bay", "Bay 1");
            startActivity(intent);
        });
        
        btnBay2.setOnClickListener(v -> {
            // Disabled bay logic if needed
        });
    }
}
