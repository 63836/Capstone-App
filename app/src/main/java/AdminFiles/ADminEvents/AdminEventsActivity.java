package AdminFiles.ADminEvents;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;

import java.util.List;

import ClientSide.EventsAndNews.EventRepository;

public class AdminEventsActivity extends AppCompatActivity {
    // Now gets the list from the repository
    public static List<EventItem> eventList = EventRepository.eventList;

    public static class EventItem {
        private String title;
        private String description;
        private String imageUri;
        private int pointsOffered;

        public EventItem(String title, String description, String imageUri, int pointsOffered) {
            this.title = title;
            this.description = description;
            this.imageUri = imageUri;
            this.pointsOffered = pointsOffered;
        }

        public String getTitle() {
            return title;
        }

        public String getDescription() {
            return description;
        }

        public String getImageUri() {
            return imageUri;
        }

        public int getPointsOffered() {
            return pointsOffered;
        }

        @Override
        public String toString() {
            return title;
        }
    }

    // This method now adds events to the central repository
    public static void addEvent(EventItem event) {
        EventRepository.addEvent(event);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_events);

        ListView eventsListView = findViewById(R.id.eventsListView);

        // We no longer need to populate the list here, as it's done in the repository.

        ArrayAdapter<EventItem> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, eventList);
        eventsListView.setAdapter(adapter);

        eventsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                EventItem selectedEvent = eventList.get(position);
                Intent intent = new Intent(AdminEventsActivity.this, AdminEventDetailActivity.class);
                intent.putExtra("eventTitle", selectedEvent.getTitle());
                intent.putExtra("eventDescription", selectedEvent.getDescription());
                intent.putExtra("eventImageUri", selectedEvent.getImageUri());
                intent.putExtra("eventPointsOffered", selectedEvent.getPointsOffered());
                startActivity(intent);
            }
        });
    }
}