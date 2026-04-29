package com.navipark.app;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class MyCarsActivity extends AppCompatActivity {
    
    private DatabaseHelper dbHelper;
    private String userEmail;
    private ArrayList<String> carsList;
    private ArrayAdapter<String> adapter;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_cars);
        
        dbHelper = new DatabaseHelper(this);
        SharedPreferences prefs = getSharedPreferences("NaviParkPrefs", MODE_PRIVATE);
        userEmail = prefs.getString("email", "");
        
        ListView lvCars = findViewById(R.id.lvCars);
        EditText etMake = findViewById(R.id.etMake);
        EditText etModel = findViewById(R.id.etModel);
        EditText etYear = findViewById(R.id.etYear);
        EditText etColor = findViewById(R.id.etColor);
        EditText etPlate = findViewById(R.id.etPlate);
        Spinner spnType = findViewById(R.id.spnType);
        Button btnAddCar = findViewById(R.id.btnAddCar);
        
        carsList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, carsList);
        lvCars.setAdapter(adapter);
        
        loadCars();
        
        btnAddCar.setOnClickListener(v -> {
            String make = etMake.getText().toString();
            String model = etModel.getText().toString();
            String year = etYear.getText().toString();
            String color = etColor.getText().toString();
            String plate = etPlate.getText().toString();
            String type = spnType.getSelectedItem().toString();
            
            if (make.isEmpty() || model.isEmpty() || plate.isEmpty()) {
                Toast.makeText(this, "Please fill required car details", Toast.LENGTH_SHORT).show();
                return;
            }
            
            if (dbHelper.addCar(userEmail, make, model, color, plate, year, type)) {
                Toast.makeText(this, "Car added!", Toast.LENGTH_SHORT).show();
                etMake.setText(""); etModel.setText(""); etYear.setText(""); etColor.setText(""); etPlate.setText("");
                loadCars();
            } else {
                Toast.makeText(this, "Error adding car", Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void loadCars() {
        carsList.clear();
        Cursor cursor = dbHelper.getCars(userEmail);
        if (cursor.moveToFirst()) {
            do {
                String make = cursor.getString(cursor.getColumnIndexOrThrow("make"));
                String model = cursor.getString(cursor.getColumnIndexOrThrow("model"));
                String plate = cursor.getString(cursor.getColumnIndexOrThrow("numberplate"));
                String type = cursor.getString(cursor.getColumnIndexOrThrow("vehicle_type"));
                carsList.add(make + " " + model + " (" + type + ") [" + plate + "]");
            } while (cursor.moveToNext());
        }
        cursor.close();
        adapter.notifyDataSetChanged();
    }
}
