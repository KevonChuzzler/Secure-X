package com.navipark.app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "navipark.db";
    private static final int DATABASE_VERSION = 3;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE users (id INTEGER PRIMARY KEY AUTOINCREMENT, email TEXT UNIQUE, password TEXT, phone TEXT, name TEXT, surname TEXT, city TEXT)");
        db.execSQL("CREATE TABLE bays (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, is_taken INTEGER)");
        db.execSQL("CREATE TABLE cars (id INTEGER PRIMARY KEY AUTOINCREMENT, email TEXT, make TEXT, model TEXT, color TEXT, numberplate TEXT, year TEXT, vehicle_type TEXT)");
        db.execSQL("CREATE TABLE bookings (id INTEGER PRIMARY KEY AUTOINCREMENT, email TEXT, bay TEXT, start_time_ms INTEGER, end_time_ms INTEGER, total_price REAL)");
        
        db.execSQL("INSERT INTO bays (name, is_taken) VALUES ('Bay 1', 0)");
        db.execSQL("INSERT INTO bays (name, is_taken) VALUES ('Bay 2', 1)");
        db.execSQL("INSERT INTO bays (name, is_taken) VALUES ('Bay 3', 0)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS users");
        db.execSQL("DROP TABLE IF EXISTS bays");
        db.execSQL("DROP TABLE IF EXISTS cars");
        db.execSQL("DROP TABLE IF EXISTS bookings");
        onCreate(db);
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

    public boolean updateProfile(String email, String phone, String name, String surname, String city) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        if (phone != null) values.put("phone", phone);
        if (name != null) values.put("name", name);
        if (surname != null) values.put("surname", surname);
        if (city != null) values.put("city", city);
        
        int rows = db.update("users", values, "email=?", new String[]{email});
        return rows > 0;
    }

    public Cursor getUserDetails(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM users WHERE email=?", new String[]{email});
    }

    public boolean addCar(String email, String make, String model, String color, String numberplate, String year, String type) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("email", email);
        values.put("make", make);
        values.put("model", model);
        values.put("color", color);
        values.put("numberplate", numberplate);
        values.put("year", year);
        values.put("vehicle_type", type);
        return db.insert("cars", null, values) != -1;
    }

    public Cursor getCars(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM cars WHERE email=?", new String[]{email});
    }

    public boolean addBooking(String email, String bay, long startMs, long endMs, double price) {
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
