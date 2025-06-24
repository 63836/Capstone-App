package ClientSide.Reports;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import com.example.myapplication.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ReportConcernActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 2;
    private static final int PERMISSIONS_REQUEST_CAMERA = 3;
    private static final int PERMISSIONS_REQUEST_READ_STORAGE = 4;


    private ImageView ivPhoto;
    private Button btnSubmit;
    private EditText etSubject, etDescription;
    private FusedLocationProviderClient fusedLocationClient;
    private GoogleMap mMap;

    private Uri photoUri;
    private String currentPhotoPath;
    private Location lastKnownLocation;

    private final ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    photoUri = result.getData().getData();
                    ivPhoto.setImageURI(photoUri);
                    // Since we get a content URI, we don't have a file path directly.
                    // The URI itself is sufficient for displaying and uploading.
                    currentPhotoPath = photoUri.toString();
                }
            });

    private final ActivityResultLauncher<Uri> cameraLauncher = registerForActivityResult(
            new ActivityResultContracts.TakePicture(),
            result -> {
                if (result) {
                    ivPhoto.setImageURI(photoUri);
                    currentPhotoPath = photoUri.toString();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_concern);

        // Initialize Views
        ivPhoto = findViewById(R.id.iv_photo);
        btnSubmit = findViewById(R.id.btn_submit);
        etSubject = findViewById(R.id.et_subject);
        etDescription = findViewById(R.id.et_description);

        // Initialize FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Initialize Map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // Set up Listeners
        ivPhoto.setOnClickListener(v -> showImagePickerDialog());
        btnSubmit.setOnClickListener(v -> submitReport());
    }

    private void showImagePickerDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose Image Source");
        builder.setItems(new CharSequence[]{"Camera", "Gallery"}, (dialog, which) -> {
            switch (which) {
                case 0:
                    checkCameraPermissionAndLaunch();
                    break;
                case 1:
                    checkStoragePermissionAndLaunchGallery();
                    break;
            }
        });
        builder.show();
    }

    private void checkCameraPermissionAndLaunch() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            launchCamera();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PERMISSIONS_REQUEST_CAMERA);
        }
    }

    private void checkStoragePermissionAndLaunchGallery() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            launchGallery();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_READ_STORAGE);
        }
    }


    private void launchCamera() {
        photoUri = createImageUri();
        if (photoUri != null) {
            cameraLauncher.launch(photoUri);
        }
    }

    private void launchGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryLauncher.launch(intent);
    }


    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        getLocationPermission();
    }

    private void getLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getDeviceLocation();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getDeviceLocation();
            } else {
                Toast.makeText(this, "Location permission is required to use this feature.", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == PERMISSIONS_REQUEST_CAMERA) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                launchCamera();
            } else {
                Toast.makeText(this, "Camera permission is required to take photos.", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == PERMISSIONS_REQUEST_READ_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                launchGallery();
            } else {
                Toast.makeText(this, "Storage permission is required to select photos.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void getDeviceLocation() {
        try {
            if (mMap == null) {
                return;
            }
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);

            fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
                if (location != null) {
                    lastKnownLocation = location;
                    LatLng currentLatLng = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());

                    // Set the map to a 3D view
                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(currentLatLng)      // Sets the center of the map to current location
                            .zoom(18)                   // Sets the zoom
                            .bearing(0)                 // Sets the orientation of the camera to north
                            .tilt(45)                   // Sets the tilt of the camera to 45 degrees
                            .build();                   // Creates a CameraPosition from the builder

                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                    mMap.addMarker(new MarkerOptions().position(currentLatLng).title("Your Location"));
                } else {
                    Toast.makeText(this, "Unable to get current location. Please ensure location is enabled.", Toast.LENGTH_LONG).show();
                }
            });
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    private Uri createImageUri() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
        return getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
    }


    private void submitReport() {
        String subject = etSubject.getText().toString().trim();
        String description = etDescription.getText().toString().trim();

        if (subject.isEmpty() || description.isEmpty() || photoUri == null) {
            Toast.makeText(this, "Please fill all fields and upload a photo.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (lastKnownLocation == null) {
            Toast.makeText(this, "Could not get location. Please wait for the map to load your location.", Toast.LENGTH_SHORT).show();
            return;
        }

        String locationString = "Location: " + lastKnownLocation.getLatitude() + ", " + lastKnownLocation.getLongitude();
        ReportsActivity.addReport(new ReportsActivity.Report(subject, description, locationString, "General Report"));
        Toast.makeText(this, "Report submitted successfully!", Toast.LENGTH_SHORT).show();
        finish();
    }
}