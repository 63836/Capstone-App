package ClientSide.EventsAndNews;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;

import java.util.List;
import java.util.ArrayList;

public class LocalNewsAlertsActivity extends AppCompatActivity {

    private RecyclerView newsRecyclerView;
    private NewsAdapter newsAdapter;

    // Static list to store news items (also used by admin to add news)
    public static List<NewsItem> newsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_news_alerts);

        newsRecyclerView = findViewById(R.id.newsRecyclerView);
        newsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Fetch news from LocalNewsAlertsActivity.newsList
        newsAdapter = new NewsAdapter(this, newsList);
        newsRecyclerView.setAdapter(newsAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh the list when the activity resumes
        newsAdapter.notifyDataSetChanged();
    }

    // Method to add a news item (called by CreateEventActivity)
    public static void addNewsItem(NewsItem newsItem) {
        newsList.add(newsItem);
    }

    // NewsItem inner class
    public static class NewsItem {
        private String title;
        private String description;
        private String date;
        private String postedBy;
        private int imageResource;

        public NewsItem(String title, String description, String date, String postedBy, int imageResource) {
            this.title = title;
            this.description = description;
            this.date = date;
            this.postedBy = postedBy;
            this.imageResource = imageResource;
        }

        public String getTitle() { return title; }
        public String getDescription() { return description; }
        public String getDate() { return date; }
        public String getPostedBy() { return postedBy; }
        public int getImageResource() { return imageResource; }
    }
}