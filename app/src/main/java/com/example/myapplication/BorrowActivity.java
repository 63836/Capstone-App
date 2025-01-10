package com.example.myapplication;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class BorrowActivity extends AppCompatActivity {

    private RecyclerView itemsRecyclerView;
    private TextInputEditText nameEditText, mobileNumberEditText, messageEditText;
    private MaterialButton submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_borrow);

        itemsRecyclerView = findViewById(R.id.itemsRecyclerView);
        nameEditText = findViewById(R.id.nameEditText);
        mobileNumberEditText = findViewById(R.id.mobileNumberEditText);
        messageEditText = findViewById(R.id.messageEditText);
        submitButton = findViewById(R.id.submitButton);

        setupRecyclerView();

        submitButton.setOnClickListener(v -> submitBorrowRequest());
    }

    private void setupRecyclerView() {
        List<BorrowItem> items = new ArrayList<>();
        items.add(new BorrowItem("Hammer", R.drawable.hammer, 5));
        items.add(new BorrowItem("Screwdriver Set", R.drawable.screwdriver_set, 3));
        items.add(new BorrowItem("Drill", R.drawable.drill, 2));
        // Add more items as needed

        BorrowItemAdapter adapter = new BorrowItemAdapter(items);
        itemsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        itemsRecyclerView.setAdapter(adapter);
    }

    private void submitBorrowRequest() {
        String name = nameEditText.getText().toString();
        String mobileNumber = mobileNumberEditText.getText().toString();
        String message = messageEditText.getText().toString();

        if (name.isEmpty() || mobileNumber.isEmpty() || message.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("Borrow Request Submitted")
                .setMessage("Thank you. Please come to the Barangay to get your borrowed item.")
                .setPositiveButton("OK", (dialog, which) -> {
                    dialog.dismiss();
                    finish();
                })
                .show();
    }

    private static class BorrowItem {
        String name;
        int imageResId;
        int availableCount;

        BorrowItem(String name, int imageResId, int availableCount) {
            this.name = name;
            this.imageResId = imageResId;
            this.availableCount = availableCount;
        }
    }

    private class BorrowItemAdapter extends RecyclerView.Adapter<BorrowItemAdapter.ViewHolder> {
        private List<BorrowItem> items;

        BorrowItemAdapter(List<BorrowItem> items) {
            this.items = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_borrow, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            BorrowItem item = items.get(position);
            holder.nameTextView.setText(item.name);
            holder.imageView.setImageResource(item.imageResId);
            holder.countTextView.setText("Available: " + item.availableCount);
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            ImageView imageView;
            TextView nameTextView;
            TextView countTextView;

            ViewHolder(View itemView) {
                super(itemView);
                imageView = itemView.findViewById(R.id.itemImageView);
                nameTextView = itemView.findViewById(R.id.itemNameTextView);
                countTextView = itemView.findViewById(R.id.itemCountTextView);
            }
        }
    }
}

