package AdminFiles.ADminEvents;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import ClientSide.EventsAndNews.LocalNewsAlertsActivity;
import ClientSide.Notifications.NotificationCenter;
import ClientSide.Notifications.NotificationItem;
import ClientSide.Notifications.NotificationStatus;
import com.example.myapplication.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CreateEventActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText titleEditText, descriptionEditText, pointsOfferedEditText;
    private Spinner categorySpinner;
    private ImageView eventImageView;
    private Button uploadImageButton, postAnnouncementButton, backButton;
    private Uri selectedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        titleEditText = findViewById(R.id.eventTitleEditText);
        descriptionEditText = findViewById(R.id.eventDescriptionEditText);
        pointsOfferedEditText = findViewById(R.id.pointsOfferedEditText);
        categorySpinner = findViewById(R.id.categorySpinner);
        eventImageView = findViewById(R.id.eventImageView);
        uploadImageButton = findViewById(R.id.uploadImageButton);
        postAnnouncementButton = findViewById(R.id.postAnnouncementButton);
        backButton = findViewById(R.id.backButton);

        // Set up spinner with "Event" and "News" options
        String[] categories = {"Event", "News"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);

        // Show the Points Offered field only when "Event" is selected
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedCategory = categorySpinner.getSelectedItem().toString();
                int visibility = selectedCategory.equals("Event") ? View.VISIBLE : View.GONE;
                pointsOfferedEditText.setVisibility(visibility);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        uploadImageButton.setOnClickListener(v -> openImagePicker());
        postAnnouncementButton.setOnClickListener(v -> postAnnouncement());
        backButton.setOnClickListener(v -> finish());
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            eventImageView.setImageURI(selectedImageUri);
        }
    }

    private void postAnnouncement() {
        String title = titleEditText.getText().toString().trim();
        String description = descriptionEditText.getText().toString().trim();
        String category = categorySpinner.getSelectedItem().toString();
        String imageUriStr = (selectedImageUri != null) ? selectedImageUri.toString() : null;

        if (title.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Title and Description are required", Toast.LENGTH_SHORT).show();
            return;
        }

        if (category.equals("Event")) {
            String pointsOfferedStr = pointsOfferedEditText.getText().toString().trim();
            if (pointsOfferedStr.isEmpty()) {
                Toast.makeText(this, "Points Offered is required for events", Toast.LENGTH_SHORT).show();
                return;
            }
            int pointsOffered = Integer.parseInt(pointsOfferedStr);
            AdminEventsActivity.addEvent(new AdminEventsActivity.EventItem(title, description, imageUriStr, pointsOffered));
            NotificationCenter.addNotification(new NotificationItem(title, "Event", imageUriStr));
            NotificationStatus.hasNewAnnouncement = true;
            Toast.makeText(this, "Event posted", Toast.LENGTH_LONG).show();
        } else if (category.equals("News")) {
            LocalNewsAlertsActivity.addNewsItem(new LocalNewsAlertsActivity.NewsItem(title, description, getCurrentDate(), "Admin", R.drawable.free_bell_icon_860_thumb));
            NotificationCenter.addNotification(new NotificationItem(title, "News", null));
            NotificationStatus.hasNewAnnouncement = true;
            Toast.makeText(this, "News alert posted", Toast.LENGTH_LONG).show();
        }

        // Clear the form
        titleEditText.setText("");
        descriptionEditText.setText("");
        pointsOfferedEditText.setText("");
        eventImageView.setImageResource(android.R.color.transparent);
        selectedImageUri = null;
    }

    private String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(new Date());
    }
}
