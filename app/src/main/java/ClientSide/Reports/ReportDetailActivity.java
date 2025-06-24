package ClientSide.Reports;

import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class ReportDetailActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private double latitude;
    private double longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_detail);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapPreview);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        ImageView detailImageView = findViewById(R.id.detailImageView);
        TextView detailTitleTextView = findViewById(R.id.detailTitleTextView);
        TextView detailTypeTextView = findViewById(R.id.detailTypeTextView);
        TextView detailLocationTextView = findViewById(R.id.detailLocationTextView);
        TextView detailDescriptionTextView = findViewById(R.id.detailDescriptionTextView);
        Button backButton = findViewById(R.id.backButton);

        String title = getIntent().getStringExtra("title");
        String type = getIntent().getStringExtra("type");
        String location = getIntent().getStringExtra("location");
        String description = getIntent().getStringExtra("description");
        String imageUriString = getIntent().getStringExtra("imageUri");
        latitude = getIntent().getDoubleExtra("latitude", 0);
        longitude = getIntent().getDoubleExtra("longitude", 0);

        detailTitleTextView.setText(title);
        detailTypeTextView.setText("Type: " + type);
        detailLocationTextView.setText(location);
        detailDescriptionTextView.setText(description);

        if (imageUriString != null) {
            Glide.with(this)
                    .load(Uri.parse(imageUriString))
                    .placeholder(R.drawable.ic_placeholder_item)
                    .into(detailImageView);
        }

        backButton.setOnClickListener(v -> finish());
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng reportLocation = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions().position(reportLocation).title("Report Location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(reportLocation, 15f));
        mMap.getUiSettings().setAllGesturesEnabled(false);
    }
}