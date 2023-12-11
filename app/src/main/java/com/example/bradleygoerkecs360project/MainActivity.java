package com.example.bradleygoerkecs360project;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.View;
import android.os.Bundle;
import android.widget.Button;
import android.content.Intent;
import android.view.MenuInflater;
import android.view.Menu;
import android.widget.EditText;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    Button buttonLogIn;
    Button buttonNewAccount;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonLogIn = findViewById(R.id.buttonLogIn);
        buttonNewAccount = findViewById(R.id.buttonNewAccount);


        // Set click listener for the log in button
        buttonLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Retrieves the the text in username and password edit text boxes IDs
                EditText UserNameEditText = findViewById(R.id.editTextUserName);
                EditText PasswordEditText = findViewById(R.id.editTextPassword);

                // Get the entered username and password
                String username = UserNameEditText.getText().toString();
                String password = PasswordEditText.getText().toString();

                // Insert the username and password into the database
                DatabaseHelper LogInDatabaseHelper = new DatabaseHelper(getApplicationContext(), "LoginDatabase");
                SQLiteDatabase db = LogInDatabaseHelper.getReadableDatabase();

                String[] columns = {"id"};
                String selection = "username = ? AND password = ?";
                String[] selectionArgs = {username, password};

                Cursor cursor = db.query("Users", columns, selection, selectionArgs, null, null, null);

                if (cursor != null && cursor.getCount() > 0) {
                    // User exists with the entered credentials
                    Toast.makeText(MainActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();

                    // Proceeds to the inventory screen
                    Intent intent = new Intent(MainActivity.this, InventoryScreen.class);
                    startActivity(intent);
                } else {
                    // No user found with the entered credentials
                    Toast.makeText(MainActivity.this, "Invalid username or password", Toast.LENGTH_SHORT).show();
                }

                // Close cursor and database connection
                if (cursor != null) {
                    cursor.close();
                }
                LogInDatabaseHelper.close();

            }
        });

        // Set click listener for the new account button
        buttonNewAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Retrieves the the text in username and password edit text boxes IDs
                EditText UserNameEditText = findViewById(R.id.editTextUserName);
                EditText PasswordEditText = findViewById(R.id.editTextPassword);

                // Get the entered username and password
                String username = UserNameEditText.getText().toString();
                String password = PasswordEditText.getText().toString();

                // Insert the username and password into the database
                DatabaseHelper LogInDatabaseHelper = new DatabaseHelper(getApplicationContext(), "LoginDatabase");
                SQLiteDatabase db = LogInDatabaseHelper.getWritableDatabase();

                ContentValues values = new ContentValues();
                values.put("username", username);
                values.put("password", password);

                long newRowId = db.insert("Users", null, values);

                // Check if insertion was successful
                if (newRowId != -1) {
                    // Data inserted successfully
                    Toast.makeText(MainActivity.this, "Account created successfully!", Toast.LENGTH_SHORT).show();
                } else {
                    // Insertion failed
                    Toast.makeText(MainActivity.this, "Failed to create account", Toast.LENGTH_SHORT).show();
                }

                LogInDatabaseHelper.close(); // Close the database connection

            }
        });

    }

}