package com.navipark.app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class BookingConfirmationActivity extends AppCompatActivity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_confirmation);
        
        TextView tvOrderLocation = findViewById(R.id.tvOrderLocation);
        TextView tvOrderSpot = findViewById(R.id.tvOrderSpot);
        TextView tvOrderDate = findViewById(R.id.tvOrderDate);
        TextView tvOrderTime = findViewById(R.id.tvOrderTime);
        TextView tvOrderDuration = findViewById(R.id.tvOrderDuration);
        TextView tvOrderTotal = findViewById(R.id.tvOrderTotal);
        Button btnBackToHome = findViewById(R.id.btnBackToHome);
        
        String location = getIntent().getStringExtra("location");
        String spot = getIntent().getStringExtra("spot");
        String date = getIntent().getStringExtra("date");
        String time = getIntent().getStringExtra("time");
        String duration = getIntent().getStringExtra("duration");
        int total = getIntent().getIntExtra("totalPrice", 0);
        
        tvOrderLocation.setText("Location: " + (location != null ? location : "N/A"));
        tvOrderSpot.setText("Spot: " + (spot != null ? spot : "N/A"));
        tvOrderDate.setText("Date: " + (date != null ? date : "N/A"));
        tvOrderTime.setText("Time: " + (time != null ? time : "N/A"));
        tvOrderDuration.setText("Duration: " + (duration != null ? duration : "N/A"));
        tvOrderTotal.setText("Total Paid: R" + total + ".00");
        
        btnBackToHome.setOnClickListener(v -> {
            Intent intent = new Intent(BookingConfirmationActivity.this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }
    
    @Override
    public void onBackPressed() {
        // Go to home instead of back to payment
        Intent intent = new Intent(BookingConfirmationActivity.this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
