package com.navipark.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
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
        CheckBox cbRememberMe = findViewById(R.id.cbRememberMe);
        Button btnLogin = findViewById(R.id.btnLogin);
        Button btnRegister = findViewById(R.id.btnRegister);
        Button btnGoogleLogin = findViewById(R.id.btnGoogleLogin);
        
        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            
            if (email.isEmpty()) {
                Toast.makeText(this, "Please enter your email address.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (password.isEmpty()) {
                Toast.makeText(this, "Please enter your password.", Toast.LENGTH_SHORT).show();
                return;
            }
            
            if (dbHelper.checkUser(email, password)) {
                loginSuccess(email, cbRememberMe.isChecked(), false);
            } else {
                Toast.makeText(this, "Invalid email or password. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
        
        btnRegister.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            
            if (email.isEmpty()) {
                Toast.makeText(this, "Please enter an email address to register.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (password.isEmpty()) {
                Toast.makeText(this, "Please create a password.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (password.length() < 6) {
                Toast.makeText(this, "Password must be at least 6 characters.", Toast.LENGTH_SHORT).show();
                return;
            }
            
            if (dbHelper.registerUser(email, password)) {
                Toast.makeText(this, "Registration Successful!", Toast.LENGTH_SHORT).show();
                loginSuccess(email, cbRememberMe.isChecked(), true);
            } else {
                Toast.makeText(this, "This email is already registered. Try logging in instead.", Toast.LENGTH_LONG).show();
            }
        });
        
        btnGoogleLogin.setOnClickListener(v -> {
            String googleEmail = "google_user@example.com";
            Toast.makeText(this, "Google Sign-In verified.", Toast.LENGTH_SHORT).show();
            
            // Check if this Google user already exists in the DB
            boolean isNewUser = !dbHelper.checkUserExists(googleEmail);
            
            if (isNewUser) {
                // Register them silently
                dbHelper.registerUser(googleEmail, "google_oauth_placeholder");
            }
            
            loginSuccess(googleEmail, cbRememberMe.isChecked(), isNewUser);
        });
    }

    private void loginSuccess(String email, boolean rememberMe, boolean isNewRegistration) {
        SharedPreferences.Editor editor = getSharedPreferences("NaviParkPrefs", MODE_PRIVATE).edit();
        editor.putBoolean("isLoggedIn", true);
        editor.putBoolean("rememberMe", rememberMe);
        editor.putString("email", email);
        editor.apply();
        
        if (isNewRegistration) {
            startActivity(new Intent(LoginActivity.this, OnboardingProfileActivity.class));
        } else {
            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
        }
        finish();
    }
}
