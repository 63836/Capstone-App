package ClientSide.EventsAndNews;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.R;
import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {

    private Context context;
    private List<LocalNewsAlertsActivity.NewsItem> newsList;

    public NewsAdapter(Context context, List<LocalNewsAlertsActivity.NewsItem> newsList) {
        this.context = context;
        this.newsList = newsList;
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_news, parent, false);
        return new NewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        LocalNewsAlertsActivity.NewsItem newsItem = newsList.get(position);
        holder.titleTextView.setText(newsItem.getTitle());
        holder.descriptionTextView.setText(newsItem.getDescription());
        holder.dateTextView.setText(newsItem.getDate());
        holder.newsImageView.setImageResource(newsItem.getImageResource());

        holder.itemView.setOnClickListener(v -> showNewsDetailsDialog(newsItem));
    }

    @Override
    public int getItemCount() {
        return newsList.size();
    }

    public static class NewsViewHolder extends RecyclerView.ViewHolder {
        ImageView newsImageView;
        TextView titleTextView, descriptionTextView, dateTextView;

        public NewsViewHolder(@NonNull View itemView) {
            super(itemView);
            newsImageView    = itemView.findViewById(R.id.newsImageView);
            titleTextView    = itemView.findViewById(R.id.titleTextView);
            descriptionTextView = itemView.findViewById(R.id.descriptionTextView);
            dateTextView     = itemView.findViewById(R.id.dateTextView);
        }
    }

    private void showNewsDetailsDialog(LocalNewsAlertsActivity.NewsItem newsItem) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View dialogView = LayoutInflater.from(context)
                .inflate(R.layout.dialog_news_details, null);
        builder.setView(dialogView);

        // bind views
        ImageView dialogImage       = dialogView.findViewById(R.id.dialogImage);
        TextView dialogTitle        = dialogView.findViewById(R.id.dialogTitle);
        TextView dialogDescription  = dialogView.findViewById(R.id.dialogDescription);
        TextView dialogDate         = dialogView.findViewById(R.id.dialogDate);
        TextView dialogSource       = dialogView.findViewById(R.id.dialogSource);
        Button closeButton          = dialogView.findViewById(R.id.closeButton);

        // populate
        dialogImage.setImageResource(newsItem.getImageResource());
        dialogTitle.setText(newsItem.getTitle());
        dialogDescription.setText(newsItem.getDescription());
        dialogDate.setText(newsItem.getDate());
        dialogSource.setText("Posted by: " + newsItem.getPostedBy());

        AlertDialog dialog = builder.create();
        closeButton.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }
}
