package com.example.bradleygoerkecs360project;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import android.view.MenuItem;
import android.Manifest;


import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.GridLayoutManager;
import android.database.sqlite.SQLiteDatabase;
import java.util.ArrayList;
import java.util.List;

import com.example.bradleygoerkecs360project.CardData;

public class InventoryScreen extends AppCompatActivity {

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private RecyclerView recyclerView;
    private CardAdapter cardAdapter;

    private static final int REQUEST_NOTIFICATION_PERMISSION = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory_screen);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerView = findViewById(R.id.recyclerView);

        createNotificationChannel();

        updateUI();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                == PackageManager.PERMISSION_GRANTED) {
            checkInventoryLevels();
        }
        else {
            // Permission hasn't been granted, request it from the user
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.POST_NOTIFICATIONS},
                    REQUEST_NOTIFICATION_PERMISSION);
        }
        //updateUIWithStaticValues();

        FloatingActionButton floatingButtonAdd = findViewById(R.id.floatingButtonAdd);

        floatingButtonAdd.setOnClickListener(v -> {

            // Proceeds to the inventory screen
            Intent intent = new Intent(InventoryScreen.this, InventoryItem.class);
            startActivity(intent);

        });

    }

    private void updateUIWithStaticValues() {
        // Create a list of static CardData objects
        //List<CardData> staticCardDataList = new ArrayList<>();
        //staticCardDataList.add(new CardData("Item 1", 10));
        //staticCardDataList.add(new CardData("Item 2", 5));
        //staticCardDataList.add(new CardData("Item 3", 15));

        // Set up the RecyclerView adapter with the static data
        //cardAdapter = new CardAdapter(staticCardDataList);
        //recyclerView.setAdapter(cardAdapter);
    }

    private void updateUI() {
        // Query the database to retrieve items
        DatabaseHelper inventoryDatabaseHelper = new DatabaseHelper(getApplicationContext(), "InventoryDatabase");
        SQLiteDatabase db = inventoryDatabaseHelper.getReadableDatabase();

        // Perform a query to get items from the database
        String[] projection = {"itemName", "quantity"};
        Cursor cursor = db.query("Items", projection, null, null, null, null, null);

        List<CardData> cardDataList = new ArrayList<>();

        if (cursor != null && cursor.moveToFirst()) {
            String[] columnNames = cursor.getColumnNames();
            for (String columnName : columnNames) {
                Log.d("ColumnNames", columnName);
            }

            do {
                // Retrieve item details from the cursor
                String itemName = cursor.getString(cursor.getColumnIndexOrThrow("itemName"));
                int itemQuantity = cursor.getInt(cursor.getColumnIndexOrThrow("quantity"));

                // Create CardData objects using the retrieved data
                CardData cardData = new CardData(itemName, itemQuantity);
                cardDataList.add(cardData);

                // Log to check the retrieved data
                Log.d("CardData", "Item Name: " + itemName + ", Quantity: " + itemQuantity);

            } while (cursor.moveToNext());

            cursor.close();
        }

        // Set up the RecyclerView adapter with the retrieved data
        cardAdapter = new CardAdapter(cardDataList, this);

        // Set the layout manager and adapter to RecyclerView
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setAdapter(cardAdapter);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_logout) {
            // Perform logout action - navigate back to the login screen
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish(); // Close the current activity
            return true;
        }
        else if (id == R.id.action_settings) {
            // Navigate to the settings screen
            Intent intent = new Intent(this, SettingsScreen.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void checkInventoryLevels() {
        DatabaseHelper inventoryDatabaseHelper = new DatabaseHelper(getApplicationContext(), "InventoryDatabase");
        SQLiteDatabase db = inventoryDatabaseHelper.getReadableDatabase();

        int threshold = 10; // Set your threshold value here
        boolean lowStock = false;

        // Perform a query to get items from the database
        Cursor cursor = db.rawQuery("SELECT itemName, quantity FROM Items WHERE quantity <= ?", new String[]{String.valueOf(threshold)});
        List<String> lowStockItems = new ArrayList<>();

        if (cursor != null && cursor.moveToFirst()) {
            do {
                // Retrieve item details from the cursor
                String itemName = cursor.getString(cursor.getColumnIndexOrThrow("itemName"));
                int itemQuantity = cursor.getInt(cursor.getColumnIndexOrThrow("quantity"));

                lowStockItems.add(itemName); // Add low stock items to a list

                // Log or handle low stock items
                Log.d("LowStock", "Item Name: " + itemName + ", Quantity: " + itemQuantity);
            } while (cursor.moveToNext());

            cursor.close();
        }

        if (!lowStockItems.isEmpty()) {
            // If low stock items exist, trigger a notification
            lowStock = true;

            // You can implement notification logic here
            // Example: Send a single notification for all low stock items
            sendLowStockNotification(lowStockItems);
        }

        if (!lowStock) {
            // Handle when no items are in low stock
            Log.d("LowStock", "No items are in low stock.");
        }

        db.close();
    }

    private void sendLowStockNotification(List<String> lowStockItems) {

        Intent intent = new Intent(this, InventoryScreen.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        // Build the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "low_stock")
                .setSmallIcon(androidx.constraintlayout.widget.R.drawable.notify_panel_notification_icon_bg)
                .setContentTitle("Low Stock Items")
                .setContentText("Some items are running low in stock!")
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("Low stock for: " + TextUtils.join(", ", lowStockItems)))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true); // Dismisses the notification when tapped

        // Display the notification using the NotificationManager
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(123, builder.build()); // 123 is a unique ID for this notification
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "LowStockChannel"; // Name for the channel
            String description = "Channel for low stock notifications"; // Description for the channel
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("low_stock", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

}