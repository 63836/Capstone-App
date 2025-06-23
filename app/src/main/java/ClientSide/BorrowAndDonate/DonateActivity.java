package ClientSide.BorrowAndDonate;

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

import com.example.myapplication.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import java.io.File;
import java.io.IOException;

public class DonateActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_GALLERY = 2;
    private String currentPhotoPath;
    private ImageView itemImageView;
    private TextInputEditText itemNameEditText, itemCountEditText, messageEditText;
    private MaterialButton capturePhotoButton, submitButton;
    private TextView placeholderText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donate);

        itemImageView = findViewById(R.id.itemImageView);
        placeholderText = findViewById(R.id.placeholderText);
        itemNameEditText = findViewById(R.id.itemNameEditText);
        itemCountEditText = findViewById(R.id.itemCountEditText);
        messageEditText = findViewById(R.id.messageEditText);
        capturePhotoButton = findViewById(R.id.capturePhotoButton);
        submitButton = findViewById(R.id.submitButton);

        capturePhotoButton.setOnClickListener(v -> showImageSourceOptions());
        submitButton.setOnClickListener(v -> submitDonation());
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

    private void dispatchGalleryIntent() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, REQUEST_GALLERY);
    }

    private File createImageFile() throws IOException {
        String timeStamp = String.valueOf(System.currentTimeMillis());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(null);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                itemImageView.setImageURI(Uri.parse(currentPhotoPath));
                placeholderText.setVisibility(View.GONE);
            } else if (requestCode == REQUEST_GALLERY) {
                Uri selectedImageUri = data.getData();
                itemImageView.setImageURI(selectedImageUri);
                placeholderText.setVisibility(View.GONE);
            }
        }
    }

    private void submitDonation() {
        String itemName = itemNameEditText.getText().toString().trim();
        String itemCount = itemCountEditText.getText().toString().trim();
        String message = messageEditText.getText().toString().trim();

        if (itemName.isEmpty() || itemCount.isEmpty() || message.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (itemName.matches("\\d+")) {
            Toast.makeText(this, "Please enter a valid item name", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            int count = Integer.parseInt(itemCount);
            if (count <= 0) {
                Toast.makeText(this, "Please enter a valid number of items", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter a valid number for item count", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(this, "Donation Submitted. Thank you very much. Please give the items to the barangay.", Toast.LENGTH_LONG).show();
        finish();
    }
}
