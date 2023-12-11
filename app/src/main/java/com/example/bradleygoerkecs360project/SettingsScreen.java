package com.example.bradleygoerkecs360project;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.content.Intent;
import android.view.MenuInflater;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;

public class SettingsScreen extends AppCompatActivity {

    Button buttonSave;
    Switch switchNotifications;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_screen);

        buttonSave = findViewById(R.id.buttonSave);
        switchNotifications = findViewById(R.id.switchNotifications);

        // Set click listener for the button
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Save the notification settings when the button is clicked
                boolean notificationsEnabled = switchNotifications.isChecked();

                // Store the notification settings in SharedPreferences
                SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean("notifications", notificationsEnabled);
                editor.apply();

                Intent intent = new Intent(SettingsScreen.this, InventoryScreen.class);
                startActivity(intent);
                //greetUser();
            }
        });
    }
}