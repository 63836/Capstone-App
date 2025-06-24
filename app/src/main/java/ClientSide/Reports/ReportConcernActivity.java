package ClientSide.Reports;

import android.Manifest;
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

import androidx.annotation.NonNull;
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

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 2;

    private ImageView ivPhoto;
    private Button btnUpload, btnSubmit;
    private EditText etSubject, etDescription;
    private FusedLocationProviderClient fusedLocationClient;
    private GoogleMap mMap;

    private String currentPhotoPath;
    private Location lastKnownLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_concern);

        // Initialize Views
        ivPhoto = findViewById(R.id.iv_photo);
        btnUpload = findViewById(R.id.btn_upload);
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
        btnUpload.setOnClickListener(v -> dispatchTakePictureIntent());
        btnSubmit.setOnClickListener(v -> submitReport());
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            ivPhoto.setImageURI(Uri.parse(currentPhotoPath));
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(null); // Use app-specific directory
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );

        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void submitReport() {
        String subject = etSubject.getText().toString().trim();
        String description = etDescription.getText().toString().trim();

        if (subject.isEmpty() || description.isEmpty() || currentPhotoPath == null || currentPhotoPath.isEmpty()) {
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