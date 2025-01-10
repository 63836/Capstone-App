package com.example.myapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ImageView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.io.File;
import java.io.IOException;

public class LostFoundReportActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private String currentPhotoPath;
    private ImageView capturedImageView;
    private String reportType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lost_found_report);

        // Retrieve the report type from the intent extra. If not provided, default to "pet".
        reportType = getIntent().getStringExtra("REPORT_TYPE");
        if (reportType == null) {
            reportType = "pet";
        }

        capturedImageView = findViewById(R.id.capturedImageView);
        MaterialButton capturePhotoButton = findViewById(R.id.capturePhotoButton);
        MaterialButton submitButton = findViewById(R.id.submitButton);

        TextInputEditText nameEditText = findViewById(R.id.nameEditText);
        TextInputEditText mobileNumberEditText = findViewById(R.id.mobileNumberEditText);
        TextInputEditText locationEditText = findViewById(R.id.locationEditText);
        TextInputEditText messageEditText = findViewById(R.id.messageEditText);

        capturePhotoButton.setOnClickListener(v -> dispatchTakePictureIntent());

        submitButton.setOnClickListener(v -> {
            String name = nameEditText.getText().toString();
            String mobileNumber = mobileNumberEditText.getText().toString();
            String location = locationEditText.getText().toString();
            String message = messageEditText.getText().toString();

            if (name.isEmpty() || mobileNumber.isEmpty() || location.isEmpty() || message.isEmpty()) {
                showAlert("Please fill in all fields");
            } else {
                submitReport(name, mobileNumber, location, message);
            }
        });
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                showAlert("Error occurred while creating the file");
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
            capturedImageView.setImageURI(Uri.parse(currentPhotoPath));
        }
    }

    private void submitReport(String name, String mobileNumber, String location, String message) {
        String popupMessage;
        if ("item".equals(reportType)) { // Null-safe comparison
            if (message.toLowerCase().contains("found")) {
                popupMessage = "Thank you for your kind service. The user will receive a notice shortly. Please surrender the item to the barangay hall. Thank you very much.";
            } else {
                popupMessage = "Your report has been logged into our system and shared with local authorities. You will be notified when it is found. Thank you and have a great day.";
            }
        } else { // pet
            if (message.toLowerCase().contains("found")) {
                popupMessage = "Thank you for your kind service. The user will receive a notice shortly. Thank you very much.";
            } else {
                popupMessage = "Your report has been logged into our system and shared with local shelters, rescue groups, and volunteers in your area. You will be notified when it is found. Thank you and have a great day.";
            }
        }

        new AlertDialog.Builder(this)
                .setTitle("Report Submitted")
                .setMessage(popupMessage)
                .setPositiveButton("OK", (dialog, which) -> {
                    dialog.dismiss();
                    finish(); // Close the activity after submission
                })
                .setCancelable(false)
                .show();
    }

    // Utility method to show an alert dialog with a given message.
    private void showAlert(String message) {
        new AlertDialog.Builder(this)
                .setTitle("Alert")
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .setCancelable(true)
                .show();
    }
}
