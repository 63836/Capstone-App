package AdminFiles.Participants;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;

public class ParticipantDetailActivity extends AppCompatActivity {

    private TextView nameTextView, genderTextView, contactTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_participant_detail);

        nameTextView = findViewById(R.id.participantDetailNameTextView);
        genderTextView = findViewById(R.id.participantDetailGenderTextView);
        contactTextView = findViewById(R.id.participantDetailContactTextView);

        String name = getIntent().getStringExtra("name");
        String gender = getIntent().getStringExtra("gender");
        String contact = getIntent().getStringExtra("contact");

        nameTextView.setText(name);
        genderTextView.setText(gender);
        contactTextView.setText(contact);
    }
}
