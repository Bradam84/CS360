package com.example.bradleygoerkecs360project;

import android.content.Intent;
import android.os.Bundle;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class InventoryItem extends AppCompatActivity {

    private EditText itemNameEditText;
    private EditText itemNumberEditText;
    private Button saveButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inventory_item);

        // Initialize UI elements
        itemNameEditText = findViewById(R.id.editTextItemName);
        itemNumberEditText = findViewById(R.id.editTextItemNumber);
        saveButton = findViewById(R.id.buttonSave);

        DatabaseHelper inventoryDatabaseHelper = new DatabaseHelper(getApplicationContext(), "InventoryDatabase");

        // Set click listener for the Save button
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the entered item name and number
                String itemName = itemNameEditText.getText().toString();
                String itemNumberStr = itemNumberEditText.getText().toString();

                // Convert item number to integer
                int itemNumber = 0;
                try {
                    itemNumber = Integer.parseInt(itemNumberStr);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }

                // Insert the values into the inventory database
                SQLiteDatabase db = inventoryDatabaseHelper.getWritableDatabase();

                ContentValues values = new ContentValues();
                if (inventoryDatabaseHelper.isItemNameUnique(itemName) ) {

                    values.put("itemName", itemName);
                    values.put("quantity", itemNumber);

                    long newRowId = db.insert("Items", null, values);

                    if (newRowId != -1) {
                        // Data inserted successfully
                        Toast.makeText(InventoryItem.this, "Item successfully added to the database", Toast.LENGTH_SHORT).show();
                    } else {
                        // Insertion failed
                        Toast.makeText(InventoryItem.this, "Item failed to be added to the database", Toast.LENGTH_SHORT).show();
                    }

                    // Close the database connection
                    inventoryDatabaseHelper.close();

                    Intent intent = new Intent(InventoryItem.this, InventoryScreen.class);
                    startActivity(intent);
                    finish();
                }
                else {
                    Toast.makeText(InventoryItem.this, "Item name already exists", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

}
