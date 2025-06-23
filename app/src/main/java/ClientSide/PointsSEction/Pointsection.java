package ClientSide.PointsSEction;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.google.android.material.card.MaterialCardView;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ClientSide.EventsAndNews.EventPageActivity;
import ClientSide.EventsAndNews.LocalNewsAlertsActivity;
import ClientSide.LoginAndSignup.MainActivity;
import ClientSide.Notifications.NotificationBottomSheet;
import ClientSide.Notifications.NotificationStatus;
import ClientSide.Reports.DisplayPhotoActivity;
import ClientSide.Reports.ReportConcernActivity;
import ClientSide.Rewards.RewardsActivity;
import ClientSide.Transaction.Transaction;
import ClientSide.Transaction.TransactionAdapter;
import ClientSide.Transaction.TransactionAdapter.ProofOfClaimActivity;
import ClientSide.Transaction.TransactionData;

public class Pointsection extends AppCompatActivity {

    private static final int EVENT_PAGE_REQUEST_CODE = 1;
    private static final int REQUEST_CAMERA_PERMISSION = 100;
    private static final int PERMISSION_REQUEST_READ_EXTERNAL_STORAGE = 1001;
    private static final int REWARDS_REQUEST_CODE = 200;

    private int currentPoints = 0;
    private TextView numberTextView;
    private MaterialCardView fileReportButton, scanButton, eventsButton, newsAlertsButton, rewardsButton;
    private String currentPhotoPath;
    private Button closeChatbot;

    // Chatbot views
    private ImageButton chatbotToggler, sendBtn;
    private View chatbotPanel;
    private EditText messageInput;
    private RecyclerView chatRecyclerView;

    // Drawer & Transaction History Views
    private DrawerLayout drawerLayout;
    private ImageButton hamburgerButton;
    private RecyclerView transactionRecyclerView;
    private TransactionAdapter transactionAdapter;
    private List<Transaction> transactionList;

    // Launcher for camera intent result
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

    // Launcher for barcode scanning
    private final ActivityResultLauncher<ScanOptions> barcodeLauncher = registerForActivityResult(
            new ScanContract(),
            result -> {
                if (result.getContents() != null) {
                    try {
                        int scannedPoints = Integer.parseInt(result.getContents());
                        addPoints(scannedPoints);
                        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new Date());
                        String uniqueCode = "TX-" + new SimpleDateFormat("yyyyMMddHHmmss", Locale.US).format(new Date());
                        TransactionData.addTransaction("QR Code Scan", scannedPoints, currentDate, uniqueCode);
                        transactionAdapter.notifyItemInserted(0);
                    } catch (NumberFormatException e) {
                        Toast.makeText(this, "Invalid QR Code. Please scan a valid point QR code.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pointsection_wrapper);

        // Drawer
        drawerLayout = findViewById(R.id.drawer_layout);
        hamburgerButton = findViewById(R.id.hamburgerButton);
        hamburgerButton.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        // Transaction history
        transactionRecyclerView = findViewById(R.id.recyclerView);
        transactionRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        transactionList = TransactionData.getTransactionList();
        transactionAdapter = new TransactionAdapter(this, transactionList, transaction -> {
            Intent intent = new Intent(Pointsection.this, ProofOfClaimActivity.class);
            intent.putExtra("TRANSACTION_DESCRIPTION", transaction.getDescription());
            intent.putExtra("TRANSACTION_AMOUNT", transaction.getAmount());
            intent.putExtra("TRANSACTION_DATE", transaction.getDate());
            intent.putExtra("TRANSACTION_CODE", transaction.getUniqueCode());
            startActivity(intent);
        });
        transactionRecyclerView.setAdapter(transactionAdapter);

        // Permissions
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    PERMISSION_REQUEST_READ_EXTERNAL_STORAGE);
        } else {
            onStoragePermissionGranted();
        }

        // Toolbar & notifications
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        View redDot = findViewById(R.id.redDot);
        View notificationFrame = findViewById(R.id.notificationFrame);
        redDot.setVisibility(NotificationStatus.hasNewAnnouncement ? View.VISIBLE : View.GONE);
        notificationFrame.setOnClickListener(v -> {
            new NotificationBottomSheet()
                    .show(getSupportFragmentManager(), "notificationBottomSheet");
        });

        // Main controls
        numberTextView = findViewById(R.id.numberTextView);
        fileReportButton = findViewById(R.id.fileReportButton);
        scanButton       = findViewById(R.id.scanButton);
        eventsButton     = findViewById(R.id.eventsButton);
        newsAlertsButton = findViewById(R.id.newsAlertsButton);
        rewardsButton    = findViewById(R.id.rewardsButton);

        fileReportButton.setOnClickListener(v -> {
            startActivity(new Intent(Pointsection.this, ReportConcernActivity.class));
            Toast.makeText(this, "Navigating to Report/Concern", Toast.LENGTH_SHORT).show();
        });

        scanButton.setOnClickListener(v -> launchScanner());

        eventsButton.setOnClickListener(v -> {
            Intent intent = new Intent(Pointsection.this, EventPageActivity.class);
            intent.putExtra("CURRENT_POINTS", currentPoints);
            startActivityForResult(intent, EVENT_PAGE_REQUEST_CODE);
        });

        newsAlertsButton.setOnClickListener(v -> {
            startActivity(new Intent(Pointsection.this, LocalNewsAlertsActivity.class));
        });

        rewardsButton.setOnClickListener(v -> {
            Intent intent = new Intent(Pointsection.this, RewardsActivity.class);
            intent.putExtra("CURRENT_POINTS", currentPoints);
            startActivityForResult(intent, REWARDS_REQUEST_CODE);
        });

        updatePointsDisplay();
    }

    @Override
    protected void onResume() {
        super.onResume();
        transactionAdapter.notifyDataSetChanged();
    }

    private void onStoragePermissionGranted() {
        // e.g. load saved images
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            startActivity(new Intent(Pointsection.this, MainActivity.class));
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EVENT_PAGE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            int updated = data.getIntExtra("UPDATED_POINTS", currentPoints);
            if (updated != currentPoints) {
                currentPoints = updated;
                updatePointsDisplay();
                Toast.makeText(this, "Claimed: " + data.getStringExtra("CLAIMED_ITEM_NAME"),
                        Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == REWARDS_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            int updated = data.getIntExtra("UPDATED_POINTS", currentPoints);
            if (updated != currentPoints) {
                currentPoints = updated;
                updatePointsDisplay();
            }
        }
    }

    private void launchScanner() {
        ScanOptions opts = new ScanOptions();
        opts.setPrompt("Scan a QR Code containing a number");
        opts.setBeepEnabled(true);
        opts.setBarcodeImageEnabled(true);
        barcodeLauncher.launch(opts);
    }

    private void addPoints(int points) {
        currentPoints += points;
        updatePointsDisplay();
        Toast.makeText(this, "Added " + points + " points", Toast.LENGTH_SHORT).show();
    }

    private void updatePointsDisplay() {
        numberTextView.setText(String.valueOf(currentPoints));
    }

    private void openCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    REQUEST_CAMERA_PERMISSION);
        } else {
            launchCamera();
        }
    }

    private void launchCamera() {
        Intent takePic = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePic.resolveActivity(getPackageManager()) != null) {
            File photoFile;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Toast.makeText(this, "Error creating file", Toast.LENGTH_SHORT).show();
                return;
            }
            Uri photoURI = FileProvider.getUriForFile(
                    this, "com.example.myapplication.fileprovider", photoFile);
            takePic.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            cameraLauncher.launch(takePic);
        }
    }

    private File createImageFile() throws IOException {
        String ts = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US)
                .format(new Date());
        File storageDir = getExternalFilesDir(null);
        File image = File.createTempFile("JPEG_" + ts + "_", ".jpg", storageDir);
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION
                && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            launchCamera();
        } else if (requestCode == PERMISSION_REQUEST_READ_EXTERNAL_STORAGE
                && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            onStoragePermissionGranted();
        } else {
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
        }
    }

    // Optional chatbot animations...
    private void showChatbotPanel() {
        chatbotPanel.setVisibility(View.VISIBLE);
        ScaleAnimation anim = new ScaleAnimation(
                0.5f, 1.0f, 0.5f, 1.0f,
                ScaleAnimation.RELATIVE_TO_SELF, 1.0f,
                ScaleAnimation.RELATIVE_TO_SELF, 1.0f);
        anim.setDuration(200);
        chatbotPanel.startAnimation(anim);
    }

    private void hideChatbotPanel() {
        ScaleAnimation anim = new ScaleAnimation(
                1.0f, 0.5f, 1.0f, 0.5f,
                ScaleAnimation.RELATIVE_TO_SELF, 1.0f,
                ScaleAnimation.RELATIVE_TO_SELF, 1.0f);
        anim.setDuration(200);
        chatbotPanel.startAnimation(anim);
        chatbotPanel.setVisibility(View.GONE);
    }
}
