package com.example.bradleygoerkecs360project;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class CardData {
    private String itemName;
    private int itemCount;
    private int itemId;

    public CardData(String name, int count) {
        this.itemName = name;
        this.itemCount = count;
    }

    public String getName() {
        return itemName;
    }

    public void setName(String name) {
        this.itemName = name;
    }

    public int getValue() {
        return itemCount;
    }

    public void setValue(int count) {
        this.itemCount = count;
    }

    public int getItemId() {
        return itemId;
    }


}
