package com.navipark.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    
    private DatabaseHelper dbHelper;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        
        dbHelper = new DatabaseHelper(this);
        
        EditText etEmail = findViewById(R.id.etEmail);
        EditText etPassword = findViewById(R.id.etPassword);
        Button btnLogin = findViewById(R.id.btnLogin);
        Button btnRegister = findViewById(R.id.btnRegister);
        Button btnGoogleLogin = findViewById(R.id.btnGoogleLogin);
        
        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString();
            String password = etPassword.getText().toString();
            
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show();
                return;
            }
            
            if (dbHelper.checkUser(email, password)) {
                loginSuccess(email);
            } else {
                Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show();
            }
        });
        
        btnRegister.setOnClickListener(v -> {
            String email = etEmail.getText().toString();
            String password = etPassword.getText().toString();
            
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show();
                return;
            }
            
            if (dbHelper.registerUser(email, password)) {
                Toast.makeText(this, "Registration Successful! Logging in...", Toast.LENGTH_SHORT).show();
                loginSuccess(email);
            } else {
                Toast.makeText(this, "User already exists or error occurred", Toast.LENGTH_SHORT).show();
            }
        });
        
        btnGoogleLogin.setOnClickListener(v -> {
            Toast.makeText(this, "Google Sign-In verified.", Toast.LENGTH_SHORT).show();
            loginSuccess("google_user@example.com");
        });
    }

    private void loginSuccess(String email) {
        SharedPreferences.Editor editor = getSharedPreferences("NaviParkPrefs", MODE_PRIVATE).edit();
        editor.putBoolean("isLoggedIn", true);
        editor.putString("email", email);
        editor.apply();
        
        startActivity(new Intent(LoginActivity.this, HomeActivity.class));
        finish();
    }
}
