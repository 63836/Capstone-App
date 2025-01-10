package com.example.myapplication;

import android.os.Bundle;
import android.widget.ImageView;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;
import java.io.File;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.button.MaterialButton;

public class DisplayPhotoActivity extends AppCompatActivity {
    private ImageView imageView;
    private TextInputEditText descriptionEditText, locationEditText;
    private MaterialButton saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_photo);

        // Set up the Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Report Issue");
        }

        imageView = findViewById(R.id.capturedImageView);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        locationEditText = findViewById(R.id.locationEditText);
        saveButton = findViewById(R.id.saveButton);

        Intent intent = getIntent();
        String photoPath = intent.getStringExtra("photoPath");

        if (photoPath != null) {
            File imgFile = new File(photoPath);
            if (imgFile.exists()) {
                Bitmap bitmap = BitmapFactory.decodeFile(photoPath);
                imageView.setImageBitmap(bitmap);
            } else {
                Toast.makeText(this, "Error loading image", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "No image path provided", Toast.LENGTH_SHORT).show();
        }

        saveButton.setOnClickListener(v -> {
            String description = descriptionEditText.getText().toString();
            String location = locationEditText.getText().toString();

            if (description.isEmpty() || location.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            } else {
                // Here you can save the description and location as needed
                // For now, we'll just show a success message
                Toast.makeText(this, "Report submitted successfully", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

