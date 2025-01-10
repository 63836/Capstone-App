package com.example.myapplication;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;

public class LocalNewsAlertsActivity extends AppCompatActivity {

    private RecyclerView newsRecyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private NewsAdapter newsAdapter;
    private List<NewsItem> newsItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_news_alerts);

        newsRecyclerView = findViewById(R.id.newsRecyclerView);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);

        newsItems = new ArrayList<>();
        newsAdapter = new NewsAdapter(this, newsItems);

        newsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        newsRecyclerView.setAdapter(newsAdapter);

        swipeRefreshLayout.setOnRefreshListener(this::refreshNews);

        // Load initial news
        loadNews();
    }

    private void loadNews() {
        newsItems.clear();
        newsItems.add(new NewsItem("Missing Person Report", "John Doe, aged 32, was last seen near Central Park wearing a red jacket and jeans.", "2023-05-15", "Police Department", R.drawable.missing_person));
        newsItems.add(new NewsItem("Wanted Criminal Alert", "A suspect involved in Double Murder and Multiple Attemted Murder. Contact authorities if seen.", "2022-05-14", "BCPO", R.drawable.wanted));
        newsItems.add(new NewsItem("Safety Tip: Home Security", "Lock all doors and windows before leaving your home. Consider installing motion-sensor lights.", "2023-05-13", "Police Department", R.drawable.safety_tips));

        newsAdapter.notifyDataSetChanged();
    }

    private void refreshNews() {
        swipeRefreshLayout.setRefreshing(true);
        newsRecyclerView.postDelayed(() -> {
            loadNews();
            swipeRefreshLayout.setRefreshing(false);
        }, 1500);
    }

    private static class NewsItem {
        String title;
        String description;
        String date;
        String source;
        int imageResId;

        NewsItem(String title, String description, String date, String source, int imageResId) {
            this.title = title;
            this.description = description;
            this.date = date;
            this.source = source;
            this.imageResId = imageResId;
        }
    }

    private class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {
        private Context context;
        private List<NewsItem> items;

        NewsAdapter(Context context, List<NewsItem> items) {
            this.context = context;
            this.items = items;
        }

        @Override
        public NewsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_news, parent, false);
            return new NewsViewHolder(view);
        }

        @Override
        public void onBindViewHolder(NewsViewHolder holder, int position) {
            NewsItem item = items.get(position);
            holder.titleTextView.setText(item.title);
            holder.descriptionTextView.setText(item.description);
            holder.dateTextView.setText(item.date);
            holder.sourceTextView.setText("Source: " + item.source);
            holder.newsImageView.setImageResource(item.imageResId);

            holder.itemView.setOnClickListener(v -> showNewsDialog(item));
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        class NewsViewHolder extends RecyclerView.ViewHolder {
            TextView titleTextView, descriptionTextView, dateTextView, sourceTextView;
            ImageView newsImageView;

            NewsViewHolder(View itemView) {
                super(itemView);
                titleTextView = itemView.findViewById(R.id.titleTextView);
                descriptionTextView = itemView.findViewById(R.id.descriptionTextView);
                dateTextView = itemView.findViewById(R.id.dateTextView);
                sourceTextView = itemView.findViewById(R.id.sourceTextView);
                newsImageView = itemView.findViewById(R.id.newsImageView);
            }
        }

        private void showNewsDialog(NewsItem item) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_news_details, null);
            builder.setView(dialogView);

            TextView titleText = dialogView.findViewById(R.id.dialogTitle);
            TextView descriptionText = dialogView.findViewById(R.id.dialogDescription);
            TextView dateText = dialogView.findViewById(R.id.dialogDate);
            TextView sourceText = dialogView.findViewById(R.id.dialogSource);
            ImageView dialogImage = dialogView.findViewById(R.id.dialogImage);

            titleText.setText(item.title);
            descriptionText.setText(item.description);
            dateText.setText("Date: " + item.date);
            sourceText.setText("Source: " + item.source);
            dialogImage.setImageResource(item.imageResId);

            builder.setPositiveButton("Close", (dialog, which) -> dialog.dismiss());

            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }
}
