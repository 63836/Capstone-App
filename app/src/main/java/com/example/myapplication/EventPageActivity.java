package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class EventPageActivity extends AppCompatActivity implements EventFragment.OnItemClaimListener {

    private ViewPager2 viewPager;
    private int currentPoints;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_page);

        currentPoints = getIntent().getIntExtra("CURRENT_POINTS", 0);

        TabLayout tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);

        EventPagerAdapter adapter = new EventPagerAdapter(this);
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            tab.setText("Event " + (position + 1));
        }).attach();
    }

    @Override
    public void onItemClaimed(String itemName, int pointsRequired) {
        if (currentPoints >= pointsRequired) {
            currentPoints -= pointsRequired;
            Toast.makeText(this, "You have claimed: " + itemName, Toast.LENGTH_SHORT).show();

            // Create an intent to pass back the updated points and claimed item name
            Intent resultIntent = new Intent();
            resultIntent.putExtra("UPDATED_POINTS", currentPoints);
            resultIntent.putExtra("CLAIMED_ITEM_NAME", itemName);
            setResult(RESULT_OK, resultIntent);

            finish();
        } else {
            Toast.makeText(this, "Not enough points to claim this item", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("UPDATED_POINTS", currentPoints);
        setResult(RESULT_OK, resultIntent);
        super.onBackPressed();
    }
}

