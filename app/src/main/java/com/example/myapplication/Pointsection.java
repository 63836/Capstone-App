package com.example.myapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class Pointsection extends AppCompatActivity {
    private static final String TAG = "Pointsection";
    private static final int EVENT_PAGE_REQUEST_CODE = 1;
    private static final int PROOF_OF_CLAIM_REQUEST_CODE = 2;

    private MaterialCardView scanButton;
    private ImageView capturePhotoButton;
    private String currentPhotoPath;
    private int currentPoints = 0;
    private TextView numberTextView;
    private RecyclerView recyclerView;
    private TransactionAdapter adapter;
    private List<Transaction> transactionList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pointsection);

        scanButton = findViewById(R.id.scanButton);
        capturePhotoButton = findViewById(R.id.capturePhotoButton);
        numberTextView = findViewById(R.id.numberTextView);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TransactionAdapter(transactionList, this::onTransactionClick);
        recyclerView.setAdapter(adapter);


        scanButton.setOnClickListener(v -> launchScanner());
        capturePhotoButton.setOnClickListener(v -> openCamera());

        MaterialCardView cardView = findViewById(R.id.eventsButton);
        cardView.setOnClickListener(v -> {
            Intent intent = new Intent(Pointsection.this, EventPageActivity.class);
            intent.putExtra("CURRENT_POINTS", currentPoints);
            startActivityForResult(intent, EVENT_PAGE_REQUEST_CODE);
        });

        updatePointsDisplay();
    }

    private void onTransactionClick(Transaction transaction) {
        Intent intent = new Intent(this, ProofOfClaimActivity.class);
        intent.putExtra("TRANSACTION_DESCRIPTION", transaction.description);
        intent.putExtra("TRANSACTION_AMOUNT", transaction.amount);
        intent.putExtra("TRANSACTION_DATE", transaction.date);
        intent.putExtra("TRANSACTION_CODE", transaction.uniqueCode);
        startActivityForResult(intent, PROOF_OF_CLAIM_REQUEST_CODE);
    }

    private void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Toast.makeText(this, "Error creating file", Toast.LENGTH_SHORT).show();
            }

            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this, "com.example.myapplication.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                cameraLauncher.launch(takePictureIntent);
            }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(null);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private final ActivityResultLauncher<Intent> cameraLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    Intent intent = new Intent(this, DisplayPhotoActivity.class);
                    intent.putExtra("photoPath", currentPhotoPath);
                    startActivity(intent);
                }
            }
    );

    private void launchScanner() {
        ScanOptions options = new ScanOptions();
        options.setPrompt("Scan a QR Code containing a number");
        options.setBeepEnabled(true);
        options.setBarcodeImageEnabled(true);
        barcodeLauncher.launch(options);
    }

    private final ActivityResultLauncher<ScanOptions> barcodeLauncher = registerForActivityResult(
            new ScanContract(),
            result -> {
                if (result.getContents() != null) {
                    try {
                        int scannedPoints = Integer.parseInt(result.getContents());
                        addPoints(scannedPoints);
                    } catch (NumberFormatException e) {
                        Toast.makeText(this, "Invalid QR Code. Please scan a valid point QR code.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    private void addPoints(int points) {
        currentPoints += points;
        updatePointsDisplay();
        addTransaction("You have successfully earned points!", points);
        Toast.makeText(this, "Added " + points + " points", Toast.LENGTH_SHORT).show();
    }

    private void updatePointsDisplay() {
        numberTextView.setText(String.valueOf(currentPoints));
    }

    private void addTransaction(String description, int amount) {
        String date = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(new Date());
        String uniqueCode = generateUniqueCode();
        Transaction transaction = new Transaction(description, amount, date, uniqueCode);
        transactionList.add(0, transaction);
        adapter.notifyItemInserted(0);
        recyclerView.scrollToPosition(0);
    }

    private String generateUniqueCode() {
        return UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EVENT_PAGE_REQUEST_CODE && resultCode == RESULT_OK) {
            int updatedPoints = data.getIntExtra("UPDATED_POINTS", currentPoints);
            if (updatedPoints != currentPoints) {
                int pointsDeducted = currentPoints - updatedPoints;
                currentPoints = updatedPoints;
                updatePointsDisplay();
                String itemName = data.getStringExtra("CLAIMED_ITEM_NAME");
                addTransaction("Claimed: " + itemName, -pointsDeducted);
            }
        }
    }

    private static class Transaction {
        String description;
        int amount;
        String date;
        String uniqueCode;

        public Transaction(String description, int amount, String date, String uniqueCode) {
            this.description = description;
            this.amount = amount;
            this.date = date;
            this.uniqueCode = uniqueCode;
        }
    }

    private static class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {
        private final List<Transaction> transactions;
        private final OnTransactionClickListener listener;

        public interface OnTransactionClickListener {
            void onTransactionClick(Transaction transaction);
        }

        public TransactionAdapter(List<Transaction> transactions, OnTransactionClickListener listener) {
            this.transactions = transactions;
            this.listener = listener;
        }

        @Override
        public TransactionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transaction, parent, false);
            return new TransactionViewHolder(view);
        }

        @Override
        public void onBindViewHolder(TransactionViewHolder holder, int position) {
            Transaction transaction = transactions.get(position);
            holder.descriptionTextView.setText(transaction.description);
            holder.amountTextView.setText(String.valueOf(Math.abs(transaction.amount)) + " Points");
            holder.dateTextView.setText(transaction.date);

            if (transaction.amount < 0) {
                holder.amountTextView.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), android.R.color.holo_red_dark));
            } else {
                holder.amountTextView.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), android.R.color.holo_green_dark));
            }

            holder.itemView.setOnClickListener(v -> listener.onTransactionClick(transaction));
        }

        @Override
        public int getItemCount() {
            return transactions.size();
        }

        static class TransactionViewHolder extends RecyclerView.ViewHolder {
            TextView descriptionTextView;
            TextView amountTextView;
            TextView dateTextView;

            public TransactionViewHolder(View itemView) {
                super(itemView);
                descriptionTextView = itemView.findViewById(R.id.descriptionTextView);
                amountTextView = itemView.findViewById(R.id.amountTextView);
                dateTextView = itemView.findViewById(R.id.dateTextView);
            }
        }
    }
}

