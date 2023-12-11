package com.example.bradleygoerkecs360project;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.Context;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String LOGIN_DATABASE = "LoginDatabase";
    private static final String INVENTORY_DATABASE = "InventoryDatabase";
    private static final int DATABASE_VERSION = 2;

    private String databaseName; // Store the database name

    public DatabaseHelper(Context context, String databaseName) {
        super(context, databaseName, null, DATABASE_VERSION);
        this.databaseName = databaseName; // Store the database name provided
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create tables for login
        if (databaseName.equals(LOGIN_DATABASE)) {
            String createLoginTableQuery = "CREATE TABLE IF NOT EXISTS Users ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "username TEXT,"
                    + "password TEXT)";
            db.execSQL(createLoginTableQuery);
        } else if (databaseName.equals(INVENTORY_DATABASE)) {
            // Create tables for inventory
            String createInventoryTableQuery = "CREATE TABLE IF NOT EXISTS Items ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "itemName TEXT,"
                    + "quantity INTEGER)";
            db.execSQL(createInventoryTableQuery);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Handle database upgrades
        if (databaseName.equals(LOGIN_DATABASE)) {
            db.execSQL("DROP TABLE IF EXISTS Users");
        } else if (databaseName.equals(INVENTORY_DATABASE)) {
            db.execSQL("DROP TABLE IF EXISTS Items");
        }
        onCreate(db);
    }

    // Method to update the count of an item in the database
    public void updateItemCount(String name, int newCount) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("quantity", newCount);

        // Update query
        db.update("Items", values, "itemName = ?", new String[]{String.valueOf(name)});
        db.close();
    }

    public void deleteItem(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("Items", "itemName = ?", new String[]{name});
        db.close();
    }

    public boolean isItemNameUnique(String itemName) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("Items", null, "itemName = ?", new String[]{itemName}, null, null, null);
        boolean isUnique = cursor.getCount() == 0;
        cursor.close();
        return isUnique;
    }

    // Access methods for the database
    public SQLiteDatabase getDatabase() {
        return this.getWritableDatabase();
    }


    public int getItemCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM Items", null);
        int count = 0;

        if (cursor != null) {
            cursor.moveToFirst();
            count = cursor.getInt(0);
            cursor.close();
        }

        return count;
    }

    // For Inventory Database
    public void onCreateInventoryDatabase(SQLiteDatabase db) {
        // Create tables for inventory
        String createInventoryTableQuery = "CREATE TABLE IF NOT EXISTS Items ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "itemName TEXT,"
                + "quantity INTEGER)";
        db.execSQL(createInventoryTableQuery);
    }

    public void onUpgradeInventoryDatabase(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Handle inventory database upgrades
        db.execSQL("DROP TABLE IF EXISTS Items");
        onCreateInventoryDatabase(db);
    }

    // Access methods for each database
    public SQLiteDatabase getLoginDatabase() {
        return this.getWritableDatabase();
    }

    public SQLiteDatabase getInventoryDatabase() {
        return this.getWritableDatabase();
    }
}
