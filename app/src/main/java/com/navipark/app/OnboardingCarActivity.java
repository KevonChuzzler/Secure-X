package com.navipark.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class OnboardingCarActivity extends AppCompatActivity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding_car);
        
        SharedPreferences prefs = getSharedPreferences("NaviParkPrefs", MODE_PRIVATE);
        String userEmail = prefs.getString("email", "");
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        
        EditText etMake = findViewById(R.id.etMake);
        EditText etModel = findViewById(R.id.etModel);
        EditText etYear = findViewById(R.id.etYear);
        EditText etColor = findViewById(R.id.etColor);
        EditText etPlate = findViewById(R.id.etPlate);
        Spinner spnType = findViewById(R.id.spnType);
        
        Button btnAddAnother = findViewById(R.id.btnAddAnother);
        Button btnFinish = findViewById(R.id.btnFinish);
        
        btnAddAnother.setOnClickListener(v -> {
            if (saveCar(dbHelper, userEmail, etMake, etModel, etYear, etColor, etPlate, spnType)) {
                Toast.makeText(this, "Vehicle Added!", Toast.LENGTH_SHORT).show();
                etMake.setText(""); etModel.setText(""); etYear.setText(""); etColor.setText(""); etPlate.setText("");
            }
        });
        
        btnFinish.setOnClickListener(v -> {
            saveCar(dbHelper, userEmail, etMake, etModel, etYear, etColor, etPlate, spnType);
            Toast.makeText(this, "Setup Complete!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(OnboardingCarActivity.this, HomeActivity.class));
            finish();
        });
    }
    
    private boolean saveCar(DatabaseHelper db, String email, EditText make, EditText model, EditText year, EditText color, EditText plate, Spinner type) {
        if (make.getText().toString().isEmpty() || plate.getText().toString().isEmpty()) return false;
        
        return db.addCar(email, 
            make.getText().toString(), 
            model.getText().toString(), 
            color.getText().toString(), 
            plate.getText().toString(), 
            year.getText().toString(), 
            type.getSelectedItem().toString()
        );
    }
}
