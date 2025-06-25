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
import com.example.myapplication.WebMapPickerActivity;
import com.google.android.material.card.MaterialCardView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ClientSide.EventsAndNews.EventPageActivity;
import ClientSide.EventsAndNews.EventRepository;
import ClientSide.EventsAndNews.LocalNewsAlertsActivity;
import ClientSide.LoginAndSignup.MainActivity;
import ClientSide.Notifications.NotificationBottomSheet;
import ClientSide.Notifications.NotificationStatus;
import ClientSide.Reports.ReportConcernActivity;
import ClientSide.Rewards.RewardsActivity;
import ClientSide.Transaction.Transaction;
import ClientSide.Transaction.TransactionAdapter;
import ClientSide.Transaction.TransactionAdapter.ProofOfClaimActivity;
import ClientSide.Transaction.TransactionData;
import AdminFiles.ADminEvents.AdminEventsActivity;

public class Pointsection extends AppCompatActivity {

    private static final int EVENT_PAGE_REQUEST_CODE = 1;
    private static final int REQUEST_CAMERA_PERMISSION = 100;
    private static final int PERMISSION_REQUEST_READ_EXTERNAL_STORAGE = 1001;
    private static final int REWARDS_REQUEST_CODE = 200;

    private int currentPoints = 0;
    private TextView numberTextView;
    private MaterialCardView fileReportButton, mapButton, eventsButton, newsAlertsButton, rewardsButton;
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
        mapButton = findViewById(R.id.mapButton);
        eventsButton     = findViewById(R.id.eventsButton);
        newsAlertsButton = findViewById(R.id.newsAlertsButton);
        rewardsButton    = findViewById(R.id.rewardsButton);

        fileReportButton.setOnClickListener(v -> {
            startActivity(new Intent(Pointsection.this, ReportConcernActivity.class));
            Toast.makeText(this, "Navigating to Report/Concern", Toast.LENGTH_SHORT).show();
        });

        mapButton.setOnClickListener(v -> {
            Intent intent = new Intent(Pointsection.this, WebMapPickerActivity.class);
            startActivity(intent);
        });

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

    private void addPoints(int points) {
        currentPoints += points;
        updatePointsDisplay();
        Toast.makeText(this, "Added " + points + " points", Toast.LENGTH_SHORT).show();
    }

    private void updatePointsDisplay() {
        numberTextView.setText(String.valueOf(currentPoints));
    }



    private File createImageFile() throws IOException {
        String ts = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US)
                .format(new Date());
        File storageDir = getExternalFilesDir(null);
        File image = File.createTempFile("JPEG_" + ts + "_", ".jpg", storageDir);
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

}