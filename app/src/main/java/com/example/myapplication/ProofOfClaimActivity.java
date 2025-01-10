package com.example.myapplication;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;

public class ProofOfClaimActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_proof_of_claim);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Proof of Claim");

        String description = getIntent().getStringExtra("TRANSACTION_DESCRIPTION");
        int amount = getIntent().getIntExtra("TRANSACTION_AMOUNT", 0);
        String date = getIntent().getStringExtra("TRANSACTION_DATE");
        String uniqueCode = getIntent().getStringExtra("TRANSACTION_CODE");

        TextView descriptionTextView = findViewById(R.id.descriptionTextView);
        TextView amountTextView = findViewById(R.id.amountTextView);
        TextView dateTextView = findViewById(R.id.dateTextView);
        TextView uniqueCodeTextView = findViewById(R.id.uniqueCodeTextView);

        descriptionTextView.setText(description);
        amountTextView.setText(String.valueOf(amount) + " Points");
        dateTextView.setText(date);
        uniqueCodeTextView.setText(uniqueCode);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}

