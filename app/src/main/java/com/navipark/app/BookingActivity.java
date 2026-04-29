package com.navipark.app;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class BookingActivity extends AppCompatActivity {
    
    private String selectedDate = "";
    private String selectedTime = "";
    private Calendar bookingCalendar = Calendar.getInstance();
    private String selectedBay = "Unknown Bay";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);
        
        selectedBay = getIntent().getStringExtra("selected_bay");
        if (selectedBay == null) selectedBay = "Bay 1";
        
        TextView tvSelectedBay = findViewById(R.id.tvSelectedBay);
        tvSelectedBay.setText("Selected Location: " + selectedBay);
        
        Button btnSelectDate = findViewById(R.id.btnSelectDate);
        Button btnSelectTime = findViewById(R.id.btnSelectTime);
        Spinner spnDuration = findViewById(R.id.spnDuration);
        Button btnConfirmBooking = findViewById(R.id.btnConfirmBooking);
        
        btnSelectDate.setOnClickListener(v -> {
            new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
                bookingCalendar.set(Calendar.YEAR, year);
                bookingCalendar.set(Calendar.MONTH, month);
                bookingCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                selectedDate = dayOfMonth + "/" + (month + 1) + "/" + year;
                btnSelectDate.setText(selectedDate);
            }, bookingCalendar.get(Calendar.YEAR), bookingCalendar.get(Calendar.MONTH), bookingCalendar.get(Calendar.DAY_OF_MONTH)).show();
        });
        
        btnSelectTime.setOnClickListener(v -> {
            new TimePickerDialog(this, (view, hourOfDay, minute) -> {
                bookingCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                bookingCalendar.set(Calendar.MINUTE, minute);
                selectedTime = hourOfDay + ":" + String.format("%02d", minute);
                btnSelectTime.setText(selectedTime);
            }, bookingCalendar.get(Calendar.HOUR_OF_DAY), bookingCalendar.get(Calendar.MINUTE), true).show();
        });
        
        btnConfirmBooking.setOnClickListener(v -> {
            if (selectedDate.isEmpty() || selectedTime.isEmpty()) {
                Toast.makeText(this, "Please select Date and Time", Toast.LENGTH_SHORT).show();
                return;
            }
            
            int durationMins = 30; // Default
            String selectedDuration = spnDuration.getSelectedItem().toString();
            switch (selectedDuration) {
                case "30 minutes": durationMins = 30; break;
                case "1 hour": durationMins = 60; break;
                case "1.5 hours": durationMins = 90; break;
                case "2 hours": durationMins = 120; break;
                case "2.5 hours": durationMins = 150; break;
                case "3 hours": durationMins = 180; break;
            }
            
            double totalPrice = calculatePrice(bookingCalendar, durationMins);
            
            long startMs = bookingCalendar.getTimeInMillis();
            long endMs = startMs + (durationMins * 60000L);
            
            SharedPreferences prefs = getSharedPreferences("NaviParkPrefs", MODE_PRIVATE);
            String userEmail = prefs.getString("email", "");
            
            DatabaseHelper dbHelper = new DatabaseHelper(BookingActivity.this);
            dbHelper.addBooking(userEmail, selectedBay, startMs, endMs, totalPrice);
            
            scheduleReminders(durationMins);
            
            Intent intent = new Intent(BookingActivity.this, PaymentActivity.class);
            intent.putExtra("date", selectedDate);
            intent.putExtra("time", selectedTime);
            intent.putExtra("totalPrice", (int) Math.round(totalPrice));
            startActivity(intent);
        });
    }

    private double calculatePrice(Calendar startCal, int totalMinutes) {
        double totalPrice = 0;
        
        for (int m = 0; m < totalMinutes; m++) {
            Calendar currentMin = (Calendar) startCal.clone();
            currentMin.add(Calendar.MINUTE, m);
            
            int dayOfWeek = currentMin.get(Calendar.DAY_OF_WEEK);
            int hourOfDay = currentMin.get(Calendar.HOUR_OF_DAY);
            int minute = currentMin.get(Calendar.MINUTE);
            double timeInDecimal = hourOfDay + (minute / 60.0);
            
            boolean isHighTraffic = false;
            if (dayOfWeek >= Calendar.MONDAY && dayOfWeek <= Calendar.FRIDAY) {
                if (timeInDecimal >= 16.5 && timeInDecimal < 19.5) {
                    isHighTraffic = true;
                }
            } else if (dayOfWeek == Calendar.SATURDAY) {
                if (timeInDecimal >= 13.0 && timeInDecimal < 21.0) {
                    isHighTraffic = true;
                }
            }
            
            totalPrice += isHighTraffic ? (35.0 / 60.0) : (25.0 / 60.0);
        }
        return totalPrice;
    }

    private void scheduleReminders(int durationMins) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (alarmManager == null) return;

        Calendar endTime = (Calendar) bookingCalendar.clone();
        endTime.add(Calendar.MINUTE, durationMins);

        Calendar reminder15 = (Calendar) endTime.clone();
        reminder15.add(Calendar.MINUTE, -15);
        
        Calendar reminder5 = (Calendar) endTime.clone();
        reminder5.add(Calendar.MINUTE, -5);

        Calendar testReminder = Calendar.getInstance();
        testReminder.add(Calendar.SECOND, 10);

        if (durationMins > 15) {
            setAlarm(alarmManager, reminder15.getTimeInMillis(), 1, "Your parking time ends in 15 minutes.", false);
        }
        setAlarm(alarmManager, reminder5.getTimeInMillis(), 2, "Your parking time ends in 5 minutes! Tap to extend.", true);
        setAlarm(alarmManager, testReminder.getTimeInMillis(), 3, "[TEST] Your parking time is almost up! Tap to extend.", true);
        
        Toast.makeText(this, "Reminders Scheduled!", Toast.LENGTH_SHORT).show();
    }

    private void setAlarm(AlarmManager alarmManager, long triggerAtMillis, int requestCode, String message, boolean isExtendable) {
        Intent intent = new Intent(this, NotificationReceiver.class);
        intent.putExtra("message", message);
        intent.putExtra("notificationId", requestCode);
        intent.putExtra("isExtendable", isExtendable);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent);
        } else {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent);
        }
    }
}
