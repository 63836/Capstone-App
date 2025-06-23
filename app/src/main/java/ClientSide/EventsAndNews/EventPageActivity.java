package ClientSide.EventsAndNews;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.myapplication.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import java.util.List;

import AdminFiles.ADminEvents.AdminEventsActivity;

public class EventPageActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_page);

        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);

        // Fetch events from AdminEventsActivity.eventList
        List<AdminEventsActivity.EventItem> events = AdminEventsActivity.eventList;
        // Four columns: title, description, imageUri, and pointsOffered (as string)
        String[][] eventData = new String[events.size()][4];

        for (int i = 0; i < events.size(); i++) {
            AdminEventsActivity.EventItem event = events.get(i);
            eventData[i][0] = event.getTitle();
            eventData[i][1] = event.getDescription();
            eventData[i][2] = event.getImageUri();
            eventData[i][3] = String.valueOf(event.getPointsOffered());
        }

        // Set up the adapter with the event data
        EventPagerAdapter adapter = new EventPagerAdapter(this, eventData);
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> tab.setText("Event " + (position + 1))
        ).attach();
    }
}
