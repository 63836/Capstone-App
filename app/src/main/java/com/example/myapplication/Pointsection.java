package com.example.myapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Pointsection extends AppCompatActivity {
    private static final int EVENT_PAGE_REQUEST_CODE = 1;

    private DrawerLayout drawerLayout;
    private RecyclerView drawerRecyclerView;
    private TransactionAdapter drawerAdapter;
    private List<Transaction> transactionList = new ArrayList<>();

    private MaterialCardView scanButton;
    private ImageView capturePhotoButton;
    private String currentPhotoPath;
    private int currentPoints = 0;
    private TextView numberTextView;
    private ImageButton hamburgerButton;
    private FloatingActionButton chatBotButton;

    // Action buttons from the main layout
    private MaterialCardView eventsButton, resourcesButton, lostFoundButton, newsAlertsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Load the wrapper layout (which includes your original main layout and the side drawer)
        setContentView(R.layout.activity_pointsection_wrapper);

        // Optional: Set up the Toolbar if available in the included layout
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Initialize the standalone hamburger button
        hamburgerButton = findViewById(R.id.hamburgerButton);
        hamburgerButton.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        // Initialize the ChatBot button and set its click listener to show the chatbot dialog
        chatBotButton = findViewById(R.id.chatBotButton);
        chatBotButton.setOnClickListener(v -> {
            Intent intent = new Intent(Pointsection.this, ChatActivity.class);
            startActivity(intent);
        });

        // Initialize the DrawerLayout and its RecyclerView for transaction history
        drawerLayout = findViewById(R.id.drawer_layout);
        drawerRecyclerView = findViewById(R.id.recyclerView);
        drawerRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        drawerAdapter = new TransactionAdapter(this, transactionList, transaction -> {
            Intent intent = new Intent(Pointsection.this, ProofOfClaimActivity.class);
            intent.putExtra("TRANSACTION_DESCRIPTION", transaction.getDescription());
            intent.putExtra("TRANSACTION_AMOUNT", transaction.getAmount());
            intent.putExtra("TRANSACTION_DATE", transaction.getDate());
            intent.putExtra("TRANSACTION_CODE", "CODE1234");
            startActivity(intent);
        });
        drawerRecyclerView.setAdapter(drawerAdapter);

        // Initialize main content views from the included layout
        numberTextView = findViewById(R.id.numberTextView);
        scanButton = findViewById(R.id.scanButton);
        capturePhotoButton = findViewById(R.id.capturePhotoButton);

        // Initialize action buttons and set their click listeners
        eventsButton = findViewById(R.id.eventsButton);
        resourcesButton = findViewById(R.id.resourcesButton);
        lostFoundButton = findViewById(R.id.lostFoundButton);
        newsAlertsButton = findViewById(R.id.newsAlertsButton);

        eventsButton.setOnClickListener(v -> {
            Intent intent = new Intent(Pointsection.this, EventPageActivity.class);
            intent.putExtra("CURRENT_POINTS", currentPoints);
            startActivityForResult(intent, EVENT_PAGE_REQUEST_CODE);
        });

        resourcesButton.setOnClickListener(v -> {
            Intent intent = new Intent(Pointsection.this, ResourcesSelectionActivity.class);
            startActivity(intent);
        });

        lostFoundButton.setOnClickListener(v -> {
            Intent intent = new Intent(Pointsection.this, LostFoundReportActivity.class);
            startActivity(intent);
        });

        newsAlertsButton.setOnClickListener(v -> {
            Intent intent = new Intent(Pointsection.this, LocalNewsAlertsActivity.class);
            startActivity(intent);
        });

        // Set up click listeners for scan and camera buttons
        scanButton.setOnClickListener(v -> launchScanner());
        capturePhotoButton.setOnClickListener(v -> openCamera());

        updatePointsDisplay();
    }

    // Handle result from EventPageActivity (for claiming an item)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EVENT_PAGE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
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
                Uri photoURI = androidx.core.content.FileProvider.getUriForFile(
                        this, "com.example.myapplication.fileprovider", photoFile);
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

    private final androidx.activity.result.ActivityResultLauncher<Intent> cameraLauncher =
            registerForActivityResult(new androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == RESULT_OK) {
                            Intent intent = new Intent(this, DisplayPhotoActivity.class);
                            intent.putExtra("photoPath", currentPhotoPath);
                            startActivity(intent);
                        }
                    });

    private void launchScanner() {
        ScanOptions options = new ScanOptions();
        options.setPrompt("Scan a QR Code containing a number");
        options.setBeepEnabled(true);
        options.setBarcodeImageEnabled(true);
        barcodeLauncher.launch(options);
    }

    private final androidx.activity.result.ActivityResultLauncher<ScanOptions> barcodeLauncher =
            registerForActivityResult(new ScanContract(),
                    result -> {
                        if (result.getContents() != null) {
                            try {
                                int scannedPoints = Integer.parseInt(result.getContents());
                                addPoints(scannedPoints);
                            } catch (NumberFormatException e) {
                                Toast.makeText(this, "Invalid QR Code. Please scan a valid point QR code.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

    private void addPoints(int points) {
        currentPoints += points;
        updatePointsDisplay();
        addTransaction("You have successfully earned points!", points);
        Toast.makeText(this, "Added " + points + " points", Toast.LENGTH_SHORT).show();
    }

    private void updatePointsDisplay() {
        numberTextView.setText(String.valueOf(currentPoints));
    }

    // Add a new transaction to the history (shown in the side drawer)
    private void addTransaction(String description, int amount) {
        String date = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(new Date());
        Transaction transaction = new Transaction(description, amount, date);
        transactionList.add(0, transaction);
        drawerAdapter.notifyItemInserted(0);
        drawerRecyclerView.scrollToPosition(0);
    }
}
