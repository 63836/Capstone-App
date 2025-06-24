package ClientSide.Reports;

import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;

public class ReportDetailActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_detail);

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
}