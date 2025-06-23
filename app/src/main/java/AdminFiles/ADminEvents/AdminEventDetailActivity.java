package AdminFiles.ADminEvents;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import AdminFiles.Participants.Participant;
import AdminFiles.Participants.ParticipantAdapter;
import AdminFiles.Participants.ParticipantDetailActivity;
import ClientSide.EventsAndNews.EventRepository;
import com.example.myapplication.R;

import java.util.List;

public class AdminEventDetailActivity extends AppCompatActivity {

    private RecyclerView participantsRecyclerView;
    private ParticipantAdapter participantAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_event_detail);

        TextView titleTextView = findViewById(R.id.eventDetailTitle);
        TextView descriptionTextView = findViewById(R.id.eventDetailDescription);
        TextView pointsTextView = findViewById(R.id.eventDetailPoints);
        participantsRecyclerView = findViewById(R.id.participantsRecyclerView);

        // Get event details from Intent extras
        String eventTitle = getIntent().getStringExtra("eventTitle");
        String eventDescription = getIntent().getStringExtra("eventDescription");
        int eventPointsOffered = getIntent().getIntExtra("eventPointsOffered", 0);

        titleTextView.setText(eventTitle);
        descriptionTextView.setText(eventDescription);
        pointsTextView.setText("Points Offered: " + eventPointsOffered);

        // Retrieve the participants list for this event
        List<Participant> participants = EventRepository.getRegistrations(eventTitle);

        // Set up RecyclerView with a custom adapter
        participantAdapter = new ParticipantAdapter(participants, new ParticipantAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Participant participant) {
                // Launch ParticipantDetailActivity when a participant item is clicked
                Intent intent = new Intent(AdminEventDetailActivity.this, ParticipantDetailActivity.class);
                intent.putExtra("name", participant.getName());
                intent.putExtra("gender", participant.getGender());
                intent.putExtra("contact", participant.getContact());
                startActivity(intent);
            }
        });
        participantsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        participantsRecyclerView.setAdapter(participantAdapter);
    }
}
