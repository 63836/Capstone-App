package ClientSide.EventsAndNews;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;

import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;

public class LocalNewsAlertsActivity extends AppCompatActivity {

    private RecyclerView newsRecyclerView;
    private NewsAdapter newsAdapter;

    public static List<NewsItem> newsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_news_alerts);

        newsRecyclerView = findViewById(R.id.newsRecyclerView);
        newsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        if (newsList.isEmpty()){
            newsList.add(new NewsItem("New Art Exhibit", "A new art exhibit is opening at the local gallery.", "2024-01-15", "Admin", R.drawable.ic_art, 16.4140, 120.5990));
            newsList.add(new NewsItem("Community Cleanup Drive", "Join the community cleanup drive this weekend.", "2024-01-20", "Admin", R.drawable.ic_cleanup, 16.4120, 120.5970));
            newsList.add(new NewsItem("Tech Talk: The Future of AI", "A tech talk on the future of AI will be held at the university.", "2024-01-22", "Admin", R.drawable.ic_tech, 16.4080, 120.5930));
            newsList.add(new NewsItem("Local Marathon", "Participate in the annual city marathon.", "2024-01-25", "Admin", R.drawable.ic_fitness, 16.4160, 120.6010));
            newsList.add(new NewsItem("Food Festival", "Enjoy a variety of cuisines at the upcoming food festival.", "2024-01-28", "Admin", R.drawable.ic_food, 16.4100, 120.5950));
        }
        newsAdapter = new NewsAdapter(this, newsList);
        newsRecyclerView.setAdapter(newsAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        newsAdapter.notifyDataSetChanged();
    }

    public static void addNewsItem(NewsItem newsItem) {
        newsList.add(newsItem);
    }

    public static List<NewsItem> getNews() {
        return newsList;
    }

    public static class NewsItem implements Serializable{
        private String title;
        private String description;
        private String date;
        private String postedBy;
        private int imageResource;
        private double latitude;
        private double longitude;

        public NewsItem(String title, String description, String date, String postedBy, int imageResource, double latitude, double longitude) {
            this.title = title;
            this.description = description;
            this.date = date;
            this.postedBy = postedBy;
            this.imageResource = imageResource;
            this.latitude = latitude;
            this.longitude = longitude;
        }

        public String getTitle() { return title; }
        public String getDescription() { return description; }
        public String getDate() { return date; }
        public String getPostedBy() { return postedBy; }
        public int getImageResource() { return imageResource; }
        public double getLatitude() { return latitude; }
        public double getLongitude() { return longitude; }
    }
}