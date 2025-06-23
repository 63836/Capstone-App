package ClientSide.Reports;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;

public class ReportDetailActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_detail);

        TextView detailTitleTextView = findViewById(R.id.detailTitleTextView);
        TextView detailTypeTextView = findViewById(R.id.detailTypeTextView);
        TextView detailLocationTextView = findViewById(R.id.detailLocationTextView);
        TextView detailDescriptionTextView = findViewById(R.id.detailDescriptionTextView);
        Button backButton = findViewById(R.id.backButton);

        String title = getIntent().getStringExtra("title");
        String type = getIntent().getStringExtra("type");
        String location = getIntent().getStringExtra("location");
        String description = getIntent().getStringExtra("description");

        detailTitleTextView.setText(title);
        detailTypeTextView.setText(type);
        detailLocationTextView.setText(location);
        detailDescriptionTextView.setText(description);

        backButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
