package com.example.myapplication;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import AdminFiles.ADminEvents.AdminEventsActivity;
import ClientSide.EventsAndNews.EventDetailsBottomSheetFragment;
import ClientSide.EventsAndNews.EventRepository;
import ClientSide.EventsAndNews.LocalNewsAlertsActivity;

public class WebMapPickerActivity extends FragmentActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private static final String TAG = "WebMapPickerActivity";
    private FusedLocationProviderClient fusedLocationClient;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private final LatLng defaultLocation = new LatLng(16.3946, 120.5977); // San Vicente, Baguio City
    private static final float DEFAULT_ZOOM = 15f;
    private Map<Marker, AdminEventsActivity.EventItem> eventMarkers = new HashMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_map_picker);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        } else {
            Log.e(TAG, "SupportMapFragment not found!");
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.setBuildingsEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);

        mMap.setOnMarkerClickListener(marker -> {
            AdminEventsActivity.EventItem eventItem = eventMarkers.get(marker);
            if (eventItem != null) {
                EventDetailsBottomSheetFragment bottomSheet = EventDetailsBottomSheetFragment.newInstance(eventItem);
                bottomSheet.show(getSupportFragmentManager(), bottomSheet.getTag());
                return true; // Consume the event
            }
            return false;
        });

        addSanVicentePolygon();
        enableMyLocation();
        addEventAndNewsMarkers();
    }

    private void addSanVicentePolygon() {
        PolygonOptions sanVicentePolygon = new PolygonOptions()
                .add(new LatLng(16.3960, 120.5960))
                .add(new LatLng(16.3965, 120.5990))
                .add(new LatLng(16.3930, 120.5995))
                .add(new LatLng(16.3925, 120.5965))
                .strokeColor(Color.RED)
                .fillColor(Color.argb(50, 255, 0, 0));
        mMap.addPolygon(sanVicentePolygon);
    }

    private void addEventAndNewsMarkers() {
        // Add markers for events
        List<AdminEventsActivity.EventItem> events = EventRepository.getEvents();
        for (AdminEventsActivity.EventItem event : events) {
            LatLng eventLocation = new LatLng(event.getLatitude(), event.getLongitude());
            Marker marker = mMap.addMarker(new MarkerOptions()
                    .position(eventLocation)
                    .title("Event: " + event.getTitle())
                    .snippet(event.getDescription())
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
            if (marker != null) {
                eventMarkers.put(marker, event);
            }
        }

        // Add markers for news
        List<LocalNewsAlertsActivity.NewsItem> newsItems = LocalNewsAlertsActivity.getNews();
        for (LocalNewsAlertsActivity.NewsItem news : newsItems) {
            LatLng newsLocation = new LatLng(news.getLatitude(), news.getLongitude());
            mMap.addMarker(new MarkerOptions()
                    .position(newsLocation)
                    .title("News: " + news.getTitle())
                    .snippet(news.getDescription())
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
        }
    }

    /**
     * Checks for location permissions and enables the My Location layer on the map.
     */
    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            if (mMap != null) {
                mMap.setMyLocationEnabled(true);
                getDeviceLocation();
            }
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableMyLocation();
            } else {
                moveCamera(defaultLocation, DEFAULT_ZOOM);
            }
        }
    }

    private void getDeviceLocation() {
        try {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {

                fusedLocationClient.getLastLocation()
                        .addOnSuccessListener(this, location -> {
                            if (location != null) {
                                LatLng currentLatLng = new LatLng(location.getLatitude(),
                                        location.getLongitude());
                                moveCamera(currentLatLng, DEFAULT_ZOOM);
                            } else {
                                Log.d(TAG, "Current location is null. Using defaults.");
                                moveCamera(defaultLocation, DEFAULT_ZOOM);
                            }
                        });
            }
        } catch (SecurityException e) {
            Log.e(TAG, "Exception: %s", e);
        }
    }

    private void moveCamera(LatLng latLng, float zoom) {
        CameraPosition position = new CameraPosition.Builder()
                .target(latLng)
                .zoom(zoom)
                .bearing(0f)
                .tilt(30f)
                .build();
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(position));
    }
}