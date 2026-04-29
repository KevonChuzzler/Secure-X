package com.navipark.app;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class SpotSelectionActivity extends AppCompatActivity {
    
    private String selectedSpot = "";
    private Button selectedButton = null;
    private String locationName = "";
    
    private static final String[] ROWS = {"A", "B", "C", "D", "E"};
    private static final int COLS = 5;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spot_selection);
        
        locationName = getIntent().getStringExtra("selected_location");
        if (locationName == null) locationName = "Unknown Location";
        
        TextView tvLocationName = findViewById(R.id.tvLocationName);
        tvLocationName.setText(locationName + " - Select a Spot");
        
        TextView tvSelectedSpot = findViewById(R.id.tvSelectedSpot);
        Button btnProceedBooking = findViewById(R.id.btnProceedBooking);
        GridLayout gridSpots = findViewById(R.id.gridSpots);
        
        // Generate some random "taken" spots for realism
        Set<String> takenSpots = new HashSet<>();
        Random random = new Random(locationName.hashCode()); // Deterministic per location
        int takenCount = 5 + random.nextInt(8);
        for (int i = 0; i < takenCount; i++) {
            String spot = ROWS[random.nextInt(ROWS.length)] + (random.nextInt(COLS) + 1);
            takenSpots.add(spot);
        }
        
        gridSpots.setColumnCount(COLS);
        gridSpots.setRowCount(ROWS.length);
        
        for (int r = 0; r < ROWS.length; r++) {
            for (int c = 0; c < COLS; c++) {
                String spotName = ROWS[r] + (c + 1);
                Button btn = new Button(this);
                btn.setText(spotName);
                btn.setTextSize(12);
                
                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.width = 0;
                params.height = GridLayout.LayoutParams.WRAP_CONTENT;
                params.columnSpec = GridLayout.spec(c, 1f);
                params.rowSpec = GridLayout.spec(r);
                params.setMargins(4, 4, 4, 4);
                btn.setLayoutParams(params);
                
                boolean isTaken = takenSpots.contains(spotName);
                
                if (isTaken) {
                    btn.setBackgroundColor(Color.parseColor("#E53935")); // Red
                    btn.setTextColor(Color.WHITE);
                    btn.setEnabled(false);
                } else {
                    btn.setBackgroundColor(Color.parseColor("#43A047")); // Green
                    btn.setTextColor(Color.WHITE);
                    
                    btn.setOnClickListener(v -> {
                        // Deselect previous
                        if (selectedButton != null) {
                            selectedButton.setBackgroundColor(Color.parseColor("#43A047"));
                        }
                        
                        // Select this one
                        selectedSpot = spotName;
                        selectedButton = btn;
                        btn.setBackgroundColor(Color.parseColor("#1565C0")); // Blue = selected
                        tvSelectedSpot.setText("Selected: " + spotName);
                    });
                }
                
                gridSpots.addView(btn);
            }
        }
        
        btnProceedBooking.setOnClickListener(v -> {
            if (selectedSpot.isEmpty()) {
                Toast.makeText(this, "Please select a parking spot first!", Toast.LENGTH_SHORT).show();
                return;
            }
            
            Intent intent = new Intent(SpotSelectionActivity.this, BookingActivity.class);
            intent.putExtra("selected_bay", locationName + " - Spot " + selectedSpot);
            intent.putExtra("selected_location", locationName);
            intent.putExtra("selected_spot", selectedSpot);
            startActivity(intent);
        });
    }
}
