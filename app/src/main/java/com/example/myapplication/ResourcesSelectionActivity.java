package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class ResourcesSelectionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resources_selection);

        MaterialButton donateButton = findViewById(R.id.donateButton);
        MaterialButton borrowButton = findViewById(R.id.borrowButton);

        donateButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, DonateActivity.class);
            startActivity(intent);
        });

        borrowButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, BorrowActivity.class);
            startActivity(intent);
        });
    }
}

