package ClientSide.Reports;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;

import com.example.myapplication.R;
import com.example.myapplication.WebMapPickerActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.io.File;
import java.io.IOException;

public class DisplayPhotoActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_GALLERY = 2;
    private static final int REQUEST_MAP_PICKER = 3;
    private ImageView imageView;
    private TextInputEditText descriptionEditText, locationEditText;
    private MaterialButton saveButton;
    private String currentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_photo);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Report Issue");
        }

        imageView = findViewById(R.id.capturedImageView);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        locationEditText = findViewById(R.id.locationEditText);
        saveButton = findViewById(R.id.saveButton);

        // Load the image passed from the previous activity.
        Intent intent = getIntent();
        String photoPath = intent.getStringExtra("photoPath");
        if (photoPath != null) {
            File imgFile = new File(photoPath);
            if (imgFile.exists()){
                Bitmap bitmap = BitmapFactory.decodeFile(photoPath);
                imageView.setImageBitmap(bitmap);
                currentPhotoPath = photoPath;
            } else {
                Toast.makeText(this, "Error loading image", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "No image provided", Toast.LENGTH_SHORT).show();
        }

        imageView.setOnClickListener(v -> showImageSourceOptions());

        // Launch map picker when location field is tapped.
        locationEditText.setFocusable(false);
        locationEditText.setOnClickListener(v -> {
            Intent intentMap = new Intent(DisplayPhotoActivity.this, WebMapPickerActivity.class);
            startActivityForResult(intentMap, REQUEST_MAP_PICKER);
        });

        saveButton.setOnClickListener(v -> {
            String description = descriptionEditText.getText().toString().trim();
            String location = locationEditText.getText().toString().trim();
            if (description.isEmpty() || location.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            } else if (description.matches("\\d+")) {
                Toast.makeText(this, "Please enter a valid description", Toast.LENGTH_SHORT).show();
            } else if (location.matches("\\d+")) {
                Toast.makeText(this, "Please enter a valid location", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Report submitted successfully", Toast.LENGTH_SHORT).show();
                finish();
            }
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
        if(takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch(IOException ex) {
                Toast.makeText(this, "Error occurred while creating file", Toast.LENGTH_SHORT).show();
            }
            if(photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.myapplication.fileprovider", photoFile);
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
        if(resultCode == RESULT_OK) {
            if(requestCode == REQUEST_IMAGE_CAPTURE) {
                imageView.setImageURI(Uri.parse(currentPhotoPath));
            } else if(requestCode == REQUEST_GALLERY) {
                Uri selectedImageUri = data.getData();
                imageView.setImageURI(selectedImageUri);
            } else if(requestCode == REQUEST_MAP_PICKER) {
                double latitude = data.getDoubleExtra("latitude", 0);
                double longitude = data.getDoubleExtra("longitude", 0);
                locationEditText.setText(latitude + ", " + longitude);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
