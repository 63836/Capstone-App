package AdminFiles.ADminEvents;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import ClientSide.EventsAndNews.LocalNewsAlertsActivity;
import ClientSide.EventsAndNews.EventRepository;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CreateEventActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private EditText titleEditText, descriptionEditText, pointsEditText, latitudeEditText, longitudeEditText;
    private ImageView eventImageView;
    private RadioGroup typeRadioGroup;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        titleEditText = findViewById(R.id.titleEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        pointsEditText = findViewById(R.id.pointsEditText);
        latitudeEditText = findViewById(R.id.latitudeEditText);
        longitudeEditText = findViewById(R.id.longitudeEditText);
        eventImageView = findViewById(R.id.eventImageView);
        typeRadioGroup = findViewById(R.id.typeRadioGroup);

        Button selectImageButton = findViewById(R.id.selectImageButton);
        selectImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        Button createButton = findViewById(R.id.createButton);
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createItem();
            }
        });
    }

    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            eventImageView.setImageURI(imageUri);
        }
    }

    private void createItem() {
        String title = titleEditText.getText().toString().trim();
        String description = descriptionEditText.getText().toString().trim();
        String pointsStr = pointsEditText.getText().toString().trim();
        String latitudeStr = latitudeEditText.getText().toString().trim();
        String longitudeStr = longitudeEditText.getText().toString().trim();

        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(description) || TextUtils.isEmpty(latitudeStr) || TextUtils.isEmpty(longitudeStr)) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        double latitude = Double.parseDouble(latitudeStr);
        double longitude = Double.parseDouble(longitudeStr);
        String imageUriStr = (imageUri != null) ? imageUri.toString() : "";

        int selectedTypeId = typeRadioGroup.getCheckedRadioButtonId();
        RadioButton selectedRadioButton = findViewById(selectedTypeId);

        if (selectedRadioButton.getText().toString().equals("Event")) {
            if (TextUtils.isEmpty(pointsStr)) {
                Toast.makeText(this, "Please enter points for the event", Toast.LENGTH_SHORT).show();
                return;
            }
            int pointsOffered = Integer.parseInt(pointsStr);
            EventRepository.addEvent(new AdminEventsActivity.EventItem(title, description, imageUriStr, pointsOffered, latitude, longitude));
            Toast.makeText(this, "Event created successfully", Toast.LENGTH_SHORT).show();
        } else {
            LocalNewsAlertsActivity.addNewsItem(new LocalNewsAlertsActivity.NewsItem(title, description, getCurrentDate(), "Admin", R.drawable.ic_default, latitude, longitude)); // Using a default icon
            Toast.makeText(this, "News created successfully", Toast.LENGTH_SHORT).show();
        }
        finish();
    }

    private String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(new Date());
    }
}