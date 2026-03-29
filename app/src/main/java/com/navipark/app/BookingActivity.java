package com.navipark.app;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class BookingActivity extends AppCompatActivity {
    
    private String selectedDate = "";
    private String selectedTime = "";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);
        
        Button btnSelectDate = findViewById(R.id.btnSelectDate);
        Button btnSelectTime = findViewById(R.id.btnSelectTime);
        EditText etDuration = findViewById(R.id.etDuration);
        CheckBox cbHighTraffic = findViewById(R.id.cbHighTraffic);
        Button btnConfirmBooking = findViewById(R.id.btnConfirmBooking);
        
        btnSelectDate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
                selectedDate = dayOfMonth + "/" + (month + 1) + "/" + year;
                btnSelectDate.setText(selectedDate);
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
        });
        
        btnSelectTime.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            new TimePickerDialog(this, (view, hourOfDay, minute) -> {
                selectedTime = hourOfDay + ":" + String.format("%02d", minute);
                btnSelectTime.setText(selectedTime);
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
        });
        
        btnConfirmBooking.setOnClickListener(v -> {
            String durationStr = etDuration.getText().toString();
            
            if (selectedDate.isEmpty() || selectedTime.isEmpty() || durationStr.isEmpty()) {
                Toast.makeText(this, "Please fill in all details (Date, Time, Hours)", Toast.LENGTH_SHORT).show();
                return;
            }
            
            int hours = Integer.parseInt(durationStr);
            int ratePerHour = cbHighTraffic.isChecked() ? 35 : 25;
            int totalPrice = hours * ratePerHour;
            
            Intent intent = new Intent(BookingActivity.this, PaymentActivity.class);
            intent.putExtra("date", selectedDate);
            intent.putExtra("time", selectedTime);
            intent.putExtra("totalPrice", totalPrice);
            startActivity(intent);
        });
    }
}
