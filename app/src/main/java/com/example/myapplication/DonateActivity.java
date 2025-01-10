package com.example.myapplication;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.io.File;
import java.io.IOException;

public class DonateActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private String currentPhotoPath;
    private ImageView itemImageView;
    private TextInputEditText itemNameEditText, itemCountEditText, messageEditText;
    private MaterialButton capturePhotoButton, submitButton;
    private TextView placeholderText; // Added field for placeholder TextView

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donate);

        itemImageView = findViewById(R.id.itemImageView);
        placeholderText = findViewById(R.id.placeholderText); // Initialize placeholder TextView
        itemNameEditText = findViewById(R.id.itemNameEditText);
        itemCountEditText = findViewById(R.id.itemCountEditText);
        messageEditText = findViewById(R.id.messageEditText);
        capturePhotoButton = findViewById(R.id.capturePhotoButton);
        submitButton = findViewById(R.id.submitButton);

        capturePhotoButton.setOnClickListener(v -> dispatchTakePictureIntent());
        submitButton.setOnClickListener(v -> submitDonation());
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Toast.makeText(this, "Error occurred while creating the file", Toast.LENGTH_SHORT).show();
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.myapplication.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = String.valueOf(System.currentTimeMillis());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(null);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            itemImageView.setImageURI(Uri.parse(currentPhotoPath));
            placeholderText.setVisibility(View.GONE); // Hide placeholder text
        }
    }

    private void submitDonation() {
        String itemName = itemNameEditText.getText().toString();
        String itemCount = itemCountEditText.getText().toString();
        String message = messageEditText.getText().toString();

        if (itemName.isEmpty() || itemCount.isEmpty() || message.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("Donation Submitted")
                .setMessage("Thank you very much. Please give the items to the barangay. Thank you.")
                .setPositiveButton("OK", (dialog, which) -> {
                    dialog.dismiss();
                    finish();
                })
                .show();
    }
}

