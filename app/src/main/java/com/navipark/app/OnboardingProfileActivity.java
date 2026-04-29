package com.navipark.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class OnboardingProfileActivity extends AppCompatActivity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding_profile);
        
        SharedPreferences prefs = getSharedPreferences("NaviParkPrefs", MODE_PRIVATE);
        String userEmail = prefs.getString("email", "");
        
        EditText etName = findViewById(R.id.etName);
        EditText etSurname = findViewById(R.id.etSurname);
        EditText etCity = findViewById(R.id.etCity);
        EditText etEmail = findViewById(R.id.etEmail);
        EditText etPhone = findViewById(R.id.etPhone);
        Button btnNext = findViewById(R.id.btnNext);
        
        etEmail.setText(userEmail);
        
        btnNext.setOnClickListener(v -> {
            String name = etName.getText().toString();
            String surname = etSurname.getText().toString();
            String city = etCity.getText().toString();
            String phone = etPhone.getText().toString();
            
            if (name.isEmpty() || surname.isEmpty() || city.isEmpty() || phone.isEmpty()) {
                Toast.makeText(this, "Please fill in all details", Toast.LENGTH_SHORT).show();
                return;
            }
            
            DatabaseHelper dbHelper = new DatabaseHelper(this);
            dbHelper.updateProfile(userEmail, phone, name, surname, city);
            
            startActivity(new Intent(OnboardingProfileActivity.this, OnboardingCarActivity.class));
            finish();
        });
    }
}
