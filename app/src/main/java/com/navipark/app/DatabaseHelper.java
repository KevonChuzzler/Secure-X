package com.navipark.app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "navipark.db";
    private static final int DATABASE_VERSION = 2; // Upgraded to v2

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE users (id INTEGER PRIMARY KEY AUTOINCREMENT, email TEXT UNIQUE, password TEXT, phone TEXT)");
        db.execSQL("CREATE TABLE bays (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, is_taken INTEGER)");
        db.execSQL("CREATE TABLE cars (id INTEGER PRIMARY KEY AUTOINCREMENT, email TEXT, make TEXT, model TEXT, color TEXT, numberplate TEXT)");
        db.execSQL("CREATE TABLE bookings (id INTEGER PRIMARY KEY AUTOINCREMENT, email TEXT, bay TEXT, start_time_ms INTEGER, end_time_ms INTEGER, total_price INTEGER)");
        
        db.execSQL("INSERT INTO bays (name, is_taken) VALUES ('Bay 1', 0)");
        db.execSQL("INSERT INTO bays (name, is_taken) VALUES ('Bay 2', 1)");
        db.execSQL("INSERT INTO bays (name, is_taken) VALUES ('Bay 3', 0)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE users ADD COLUMN phone TEXT");
            db.execSQL("CREATE TABLE cars (id INTEGER PRIMARY KEY AUTOINCREMENT, email TEXT, make TEXT, model TEXT, color TEXT, numberplate TEXT)");
            db.execSQL("CREATE TABLE bookings (id INTEGER PRIMARY KEY AUTOINCREMENT, email TEXT, bay TEXT, start_time_ms INTEGER, end_time_ms INTEGER, total_price INTEGER)");
        }
    }

    public boolean registerUser(String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("email", email);
        values.put("password", password);
        long result = db.insert("users", null, values);
        return result != -1;
    }

    public boolean checkUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM users WHERE email=? AND password=?", new String[]{email, password});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    public boolean updatePhone(String email, String phone) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("phone", phone);
        int rows = db.update("users", values, "email=?", new String[]{email});
        return rows > 0;
    }

    public String getPhone(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT phone FROM users WHERE email=?", new String[]{email});
        String phone = "";
        if (cursor.moveToFirst()) {
            phone = cursor.getString(0);
        }
        cursor.close();
        return phone != null ? phone : "";
    }

    public boolean addCar(String email, String make, String model, String color, String numberplate) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("email", email);
        values.put("make", make);
        values.put("model", model);
        values.put("color", color);
        values.put("numberplate", numberplate);
        return db.insert("cars", null, values) != -1;
    }

    public Cursor getCars(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM cars WHERE email=?", new String[]{email});
    }

    public boolean addBooking(String email, String bay, long startMs, long endMs, int price) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("email", email);
        values.put("bay", bay);
        values.put("start_time_ms", startMs);
        values.put("end_time_ms", endMs);
        values.put("total_price", price);
        return db.insert("bookings", null, values) != -1;
    }

    public Cursor getActiveBookings(String email, long currentTimeMs) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM bookings WHERE email=? AND end_time_ms > ? ORDER BY end_time_ms ASC", 
            new String[]{email, String.valueOf(currentTimeMs)});
    }
}
