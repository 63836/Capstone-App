package AdminFiles.ADminEvents;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.R;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AdminEventsActivity extends AppCompatActivity {

    private RecyclerView eventsRecyclerView;
    private Button addEventButton;
    private static List<EventItem> eventList = new ArrayList<>();
    private AdminEventAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_events);

        eventsRecyclerView = findViewById(R.id.eventsRecyclerView);
        addEventButton = findViewById(R.id.addEventButton);

        adapter = new AdminEventAdapter(eventList, this::onEventClick);
        eventsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        eventsRecyclerView.setAdapter(adapter);

        addEventButton.setOnClickListener(v -> {
            Intent intent = new Intent(AdminEventsActivity.this, CreateEventActivity.class);
            startActivity(intent);
        });
    }

    private void onEventClick(EventItem eventItem) {
        Intent intent = new Intent(this, AdminEventDetailActivity.class);
        intent.putExtra("selected_event", eventItem);
        startActivity(intent);
    }

    public static class EventItem implements Serializable {
        private String title;
        private String description;
        private int pointsOffered;
        private String imageUrl;
        private String date;
        private String time;

        public EventItem(String title, String description, int pointsOffered, String imageUrl, String date, String time) {
            this.title = title;
            this.description = description;
            this.pointsOffered = pointsOffered;
            this.imageUrl = imageUrl;
            this.date = date;
            this.time = time;
        }

        // Getters
        public String getTitle() { return title; }
        public String getDescription() { return description; }
        public int getPointsOffered() { return pointsOffered; }
        public String getImageUrl() { return imageUrl; }
        public String getDate() { return date; }
        public String getTime() { return time; }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh the list from the source (e.g., EventRepository)
        eventList.clear();
        eventList.addAll(getAllEvents());
        adapter.notifyDataSetChanged();
    }

    private List<EventItem> getAllEvents() {
        // This should be replaced with actual data fetching logic
        return new ArrayList<>();
    }
}