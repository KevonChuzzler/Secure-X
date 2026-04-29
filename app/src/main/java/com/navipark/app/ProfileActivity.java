package com.navipark.app;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {
    
    private DatabaseHelper dbHelper;
    private String userEmail;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        
        dbHelper = new DatabaseHelper(this);
        SharedPreferences prefs = getSharedPreferences("NaviParkPrefs", MODE_PRIVATE);
        userEmail = prefs.getString("email", "");
        
        EditText etProfileEmail = findViewById(R.id.etProfileEmail);
        EditText etProfilePhone = findViewById(R.id.etProfilePhone);
        Button btnSaveProfile = findViewById(R.id.btnSaveProfile);
        
        etProfileEmail.setText(userEmail);
        etProfilePhone.setText(dbHelper.getPhone(userEmail));
        
        btnSaveProfile.setOnClickListener(v -> {
            String phone = etProfilePhone.getText().toString();
            if (dbHelper.updatePhone(userEmail, phone)) {
                Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Error updating profile.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
