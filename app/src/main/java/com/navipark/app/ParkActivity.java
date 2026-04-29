package com.navipark.app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class ParkActivity extends AppCompatActivity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_park);
        
        ListView lvLocations = findViewById(R.id.lvLocations);
        
        ArrayList<String> locations = new ArrayList<>();
        locations.add("i'langa Mall - 1.2km");
        locations.add("Nelspruit CBD - 2.5km");
        locations.add("Riverside Mall - 3.8km");
        locations.add("Kruger Mpumalanga Int. Airport - 22km");
        
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, locations);
        lvLocations.setAdapter(adapter);
        
        lvLocations.setOnItemClickListener((parent, view, position, id) -> {
            String selectedLocation = locations.get(position).split(" - ")[0];
            Intent intent = new Intent(ParkActivity.this, SpotSelectionActivity.class);
            intent.putExtra("selected_location", selectedLocation);
            startActivity(intent);
        });
    }
}
