package ClientSide.Reports;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.example.myapplication.R;
import com.example.myapplication.WebMapPickerActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.io.File;
import java.io.IOException;

public class LostFoundReportActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_GALLERY = 2;
    private static final int REQUEST_MAP_PICKER = 3;
    private String currentPhotoPath;
    private ImageView capturedImageView;
    private String reportType;
    private TextInputEditText locationEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lost_found_report);

        reportType = getIntent().getStringExtra("REPORT_TYPE");
        if (reportType == null) {
            reportType = "pet";
        }

        capturedImageView = findViewById(R.id.capturedImageView);
        MaterialButton capturePhotoButton = findViewById(R.id.capturePhotoButton);
        MaterialButton submitButton = findViewById(R.id.submitButton);

        TextInputEditText nameEditText = findViewById(R.id.nameEditText);
        TextInputEditText mobileNumberEditText = findViewById(R.id.mobileNumberEditText);
        locationEditText = findViewById(R.id.locationEditText);
        TextInputEditText messageEditText = findViewById(R.id.messageEditText);

        capturePhotoButton.setOnClickListener(v -> showImageSourceOptions());

        // Launch the map picker activity when location is tapped.
        locationEditText.setFocusable(false);
        locationEditText.setOnClickListener(v -> {
            Intent intent = new Intent(LostFoundReportActivity.this, WebMapPickerActivity.class);
            startActivityForResult(intent, REQUEST_MAP_PICKER);
        });

        submitButton.setOnClickListener(v -> {
            String name = nameEditText.getText().toString().trim();
            String mobileNumber = mobileNumberEditText.getText().toString().trim();
            String location = locationEditText.getText().toString().trim();
            String message = messageEditText.getText().toString().trim();

            if (name.isEmpty() || mobileNumber.isEmpty() || location.isEmpty() || message.isEmpty()) {
                showToast("Please fill in all fields");
                return;
            }
            if (name.matches("\\d+")) {
                showToast("Please enter a valid name");
                return;
            }
            if (!mobileNumber.matches("\\d{11}")) {
                showToast("Please enter a valid 11-digit mobile number");
                return;
            }
            if (location.matches("\\d+")) {
                showToast("Please enter a valid location");
                return;
            }

            submitReport(name, mobileNumber, location, message);
        });
    }

    private void showImageSourceOptions() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Image Source");
        String[] options = {"Take Photo", "Choose from Gallery"};
        builder.setItems(options, (dialog, which) -> {
            if (which == 0) {
                dispatchTakePictureIntent();
            } else if (which == 1) {
                dispatchGalleryIntent();
            }
        });
        builder.show();
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                showToast("Error occurred while creating the file");
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this, "com.example.myapplication.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private void dispatchGalleryIntent() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, REQUEST_GALLERY);
    }

    private File createImageFile() throws IOException {
        String timeStamp = String.valueOf(System.currentTimeMillis());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(null);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                capturedImageView.setImageURI(Uri.parse(currentPhotoPath));
            } else if (requestCode == REQUEST_GALLERY) {
                Uri selectedImageUri = data.getData();
                capturedImageView.setImageURI(selectedImageUri);
            } else if (requestCode == REQUEST_MAP_PICKER) {
                double latitude = data.getDoubleExtra("latitude", 0);
                double longitude = data.getDoubleExtra("longitude", 0);
                locationEditText.setText(latitude + ", " + longitude);
            }
        }
    }

    private void submitReport(String name, String mobileNumber, String location, String message) {
        String popupMessage;
        if ("item".equals(reportType)) {
            if (message.toLowerCase().contains("found")) {
                popupMessage = "Thank you for your kind service. The user will receive a notice shortly. Please surrender the item to the barangay hall. Thank you very much.";
            } else {
                popupMessage = "Your report has been logged into our system and shared with local authorities. You will be notified when it is found. Thank you and have a great day.";
            }
        } else {
            if (message.toLowerCase().contains("found")) {
                popupMessage = "Thank you for your kind service. The user will receive a notice shortly. Thank you very much.";
            } else {
                popupMessage = "Your report has been logged into our system and shared with local shelters, rescue groups, and volunteers in your area. You will be notified when it is found. Thank you and have a great day.";
            }
        }
        // Create a new report and add it to the ReportsActivity static list.
        ReportsActivity.addReport(new ReportsActivity.Report("Lost Found Report: " + name, message, location, "Lost and Found"));
        Toast.makeText(this, popupMessage, Toast.LENGTH_LONG).show();
        finish();
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
