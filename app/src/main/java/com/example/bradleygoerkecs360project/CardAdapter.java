package com.example.bradleygoerkecs360project;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.CardViewHolder> {
    private final Context context;
    private List<CardData> cardDataList;
    private int itemId;
    private String itemName;

    public CardAdapter(List<CardData> dataList, Context context) {
        this.cardDataList = dataList;
        this.context = context;
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview, parent, false);
        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        CardData data = cardDataList.get(position);
        holder.textViewName.setText(data.getName());
        holder.textViewCount.setText(String.valueOf(data.getValue()));

        // Click listeners for the buttons

        holder.buttonAdd.setOnClickListener(v -> {
            // Adds 1 to the count
            int newValue = data.getValue() + 1; // Increment the count

            // Update the database
            DatabaseHelper dbHelper = new DatabaseHelper(context, "InventoryDatabase");
            dbHelper.updateItemCount(data.getName(), newValue);

            // Update the CardData object and TextView
            data.setValue(newValue);
            holder.textViewCount.setText(String.valueOf(newValue));

        });

        holder.buttonSubtract.setOnClickListener(v -> {
            // Subtracts 1 from the count
            int newValue = data.getValue() - 1;

            // Update the database
            DatabaseHelper dbHelper = new DatabaseHelper(context, "InventoryDatabase");
            dbHelper.updateItemCount(data.getName(), newValue);

            // Update the CardData object and TextView
            data.setValue(newValue);
            holder.textViewCount.setText(String.valueOf(newValue));
        });

        holder.buttonDelete.setOnClickListener(v -> {
            // Deletes item from the database
            String itemName = holder.textViewName.getText().toString();

            DatabaseHelper dbHelper = new DatabaseHelper(context, "InventoryDatabase");
            dbHelper.deleteItem(itemName);

            // Removes the item from the recyclerview
            cardDataList.remove(position);
            notifyItemRemoved(position);
        });
    }

    @Override
    public int getItemCount() {
        return cardDataList.size();
    }

    public static class CardViewHolder extends RecyclerView.ViewHolder {

        TextView textViewName;
        TextView textViewCount;
        Button buttonAdd;
        Button buttonSubtract;
        Button buttonDelete;

        public CardViewHolder(View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.textViewName);
            textViewCount = itemView.findViewById(R.id.textViewCount);
            buttonAdd = itemView.findViewById(R.id.buttonAdd);
            buttonSubtract = itemView.findViewById(R.id.buttonSubtract);
            buttonDelete = itemView.findViewById(R.id.buttonDelete);

        }
    }
}

