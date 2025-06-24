package AdminFiles.MainDashboard;

import android.content.Intent;
import android.os.Bundle;

import com.example.myapplication.R;

import AdminFiles.AddminRewards.AdminAddRewardActivity;
import AdminFiles.ADminEvents.AdminEventsActivity;
import AdminFiles.ADminEvents.CreateEventActivity;
import ClientSide.Reports.ReportsActivity;
import com.google.android.material.card.MaterialCardView;
import androidx.appcompat.app.AppCompatActivity;

import ClientSide.LoginAndSignup.MainActivity;

public class AdminActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        MaterialCardView createEventCard = findViewById(R.id.createEventButton);
        MaterialCardView viewReportsCard = findViewById(R.id.viewReportsButton);
        MaterialCardView addRewardButton = findViewById(R.id.addRewardButton);
        MaterialCardView viewEventsButton = findViewById(R.id.viewEventsButton);

        createEventCard.setOnClickListener(v -> {
            Intent intent = new Intent(AdminActivity.this, CreateEventActivity.class);
            startActivity(intent);
        });

        viewReportsCard.setOnClickListener(v -> {
            Intent intent = new Intent(AdminActivity.this, ReportsActivity.class);
            startActivity(intent);
        });

        addRewardButton.setOnClickListener(v -> {
            Intent intent = new Intent(AdminActivity.this, AdminAddRewardActivity.class);
            startActivity(intent);
        });

        viewEventsButton.setOnClickListener(v -> {
            Intent intent = new Intent(AdminActivity.this, AdminEventsActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(AdminActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}