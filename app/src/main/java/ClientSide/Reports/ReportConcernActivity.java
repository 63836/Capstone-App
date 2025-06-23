package ClientSide.Reports;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import ClientSide.BorrowAndDonate.ResourcesSelectionActivity;
import com.google.android.material.card.MaterialCardView;

public class ReportConcernActivity extends AppCompatActivity {

    private MaterialCardView lostFoundReportButton, resourcesSelectionButton, displayPhotoButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_concern);

        lostFoundReportButton = findViewById(R.id.lostFoundReportButton);
        resourcesSelectionButton = findViewById(R.id.resourcesSelectionButton);
        displayPhotoButton = findViewById(R.id.displayPhotoButton);

        lostFoundReportButton.setOnClickListener(v -> {
            // Redirect to LostFoundReportActivity
            Intent intent = new Intent(ReportConcernActivity.this, LostFoundReportActivity.class);
            startActivity(intent);
            Toast.makeText(this, "Navigating to Lost & Found Report", Toast.LENGTH_SHORT).show();
        });

        resourcesSelectionButton.setOnClickListener(v -> {
            // Redirect to ResourcesSelectionActivity
            Intent intent = new Intent(ReportConcernActivity.this, ResourcesSelectionActivity.class);
            startActivity(intent);
            Toast.makeText(this, "Navigating to Resources Selection", Toast.LENGTH_SHORT).show();
        });

        displayPhotoButton.setOnClickListener(v -> {
            // Redirect to DisplayPhotoActivity
            Intent intent = new Intent(ReportConcernActivity.this, DisplayPhotoActivity.class);
            startActivity(intent);
            Toast.makeText(this, "Navigating to Display Photo", Toast.LENGTH_SHORT).show();
        });
    }
}
