package com.navipark.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {
    
    private DatabaseHelper dbHelper;
    private String userEmail;
    private ArrayList<String> bookingsList;
    private ArrayAdapter<String> adapter;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        
        dbHelper = new DatabaseHelper(this);
        SharedPreferences prefs = getSharedPreferences("NaviParkPrefs", MODE_PRIVATE);
        userEmail = prefs.getString("email", "");
        
        Button btnProfile = findViewById(R.id.btnProfile);
        Button btnMyCars = findViewById(R.id.btnMyCars);
        Button btnBay1 = findViewById(R.id.btnBay1); 
        ListView lvActiveBookings = findViewById(R.id.lvActiveBookings);
        
        bookingsList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, bookingsList);
        lvActiveBookings.setAdapter(adapter);
        
        btnProfile.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, ProfileActivity.class)));
        btnMyCars.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, MyCarsActivity.class)));
        
        btnBay1.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, BookingActivity.class);
            intent.putExtra("selected_bay", "Bay 1");
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadActiveBookings();
    }
    
    private void loadActiveBookings() {
        bookingsList.clear();
        long now = System.currentTimeMillis();
        Cursor cursor = dbHelper.getActiveBookings(userEmail, now);
        if (cursor.moveToFirst()) {
            do {
                String bay = cursor.getString(cursor.getColumnIndexOrThrow("bay"));
                long endMs = cursor.getLong(cursor.getColumnIndexOrThrow("end_time_ms"));
                long minsLeft = (endMs - now) / 60000;
                
                if (minsLeft > 0) {
                    bookingsList.add(bay + " - " + minsLeft + " mins left");
                }
            } while (cursor.moveToNext());
        } else {
            bookingsList.add("No active bookings.");
        }
        cursor.close();
        adapter.notifyDataSetChanged();
    }
}
