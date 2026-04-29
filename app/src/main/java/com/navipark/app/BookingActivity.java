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
    
    private boolean dateSelected = false;
    private boolean timeSelected = false;
    private String selectedDate = "";
    private String selectedTime = "";
    private Calendar bookingCalendar = Calendar.getInstance();
    private String selectedBay = "";
    private String selectedLocation = "";
    private String selectedSpot = "";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);
        
        selectedBay = getIntent().getStringExtra("selected_bay");
        selectedLocation = getIntent().getStringExtra("selected_location");
        selectedSpot = getIntent().getStringExtra("selected_spot");
        if (selectedBay == null) selectedBay = "Bay 1";
        if (selectedLocation == null) selectedLocation = "";
        if (selectedSpot == null) selectedSpot = "";
        
        TextView tvSelectedBay = findViewById(R.id.tvSelectedBay);
        tvSelectedBay.setText("Booking: " + selectedBay);
        
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
                dateSelected = true;
                btnSelectDate.setText(selectedDate);
            }, bookingCalendar.get(Calendar.YEAR), bookingCalendar.get(Calendar.MONTH), bookingCalendar.get(Calendar.DAY_OF_MONTH)).show();
        });
        
        btnSelectTime.setOnClickListener(v -> {
            if (!dateSelected) {
                Toast.makeText(this, "Please select a date first!", Toast.LENGTH_SHORT).show();
                return;
            }
            new TimePickerDialog(this, (view, hourOfDay, minute) -> {
                bookingCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                bookingCalendar.set(Calendar.MINUTE, minute);
                selectedTime = hourOfDay + ":" + String.format("%02d", minute);
                timeSelected = true;
                btnSelectTime.setText(selectedTime);
            }, bookingCalendar.get(Calendar.HOUR_OF_DAY), bookingCalendar.get(Calendar.MINUTE), true).show();
        });
        
        btnConfirmBooking.setOnClickListener(v -> {
            // Step-by-step validation with clear messages
            if (!dateSelected) {
                Toast.makeText(this, "Please choose a date before proceeding.", Toast.LENGTH_LONG).show();
                return;
            }
            if (!timeSelected) {
                Toast.makeText(this, "Please choose a time before proceeding.", Toast.LENGTH_LONG).show();
                return;
            }
            
            // Check date is not in the past
            Calendar now = Calendar.getInstance();
            if (bookingCalendar.before(now)) {
                Toast.makeText(this, "You cannot book in the past! Please select a future date and time.", Toast.LENGTH_LONG).show();
                return;
            }
            
            String selectedDuration = spnDuration.getSelectedItem().toString();
            int durationMins = parseDuration(selectedDuration);
            
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
            intent.putExtra("duration", selectedDuration);
            intent.putExtra("location", selectedLocation);
            intent.putExtra("spot", selectedSpot);
            intent.putExtra("totalPrice", (int) Math.round(totalPrice));
            startActivity(intent);
        });
    }

    private int parseDuration(String durationStr) {
        switch (durationStr) {
            case "30 minutes": return 30;
            case "1 hour": return 60;
            case "1.5 hours": return 90;
            case "2 hours": return 120;
            case "2.5 hours": return 150;
            case "3 hours": return 180;
            default: return 30;
        }
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
