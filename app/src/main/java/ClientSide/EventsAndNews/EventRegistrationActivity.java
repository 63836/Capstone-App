package ClientSide.EventsAndNews;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;

import AdminFiles.Participants.Participant;

public class EventRegistrationActivity extends AppCompatActivity {

    private EditText nameEditText, genderEditText, contactEditText;
    private Button registerButton;
    private String eventTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_registration);

        eventTitle = getIntent().getStringExtra("eventTitle");

        nameEditText = findViewById(R.id.regNameEditText);
        genderEditText = findViewById(R.id.regGenderEditText);
        contactEditText = findViewById(R.id.regContactEditText);
        registerButton = findViewById(R.id.regRegisterButton);

        registerButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                registerForEvent();
            }
        });
    }

    private void registerForEvent() {
        String name = nameEditText.getText().toString().trim();
        String gender = genderEditText.getText().toString().trim();
        String contact = contactEditText.getText().toString().trim();

        if(name.isEmpty() || gender.isEmpty() || contact.isEmpty()){
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        Participant participant = new Participant(name, gender, contact);
        EventRepository.addRegistration(eventTitle, participant);
        Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show();
        finish();
    }
}
