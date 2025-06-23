package ClientSide.Notifications;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import ClientSide.EventsAndNews.LocalNewsAlertsActivity;
import com.example.myapplication.R;

import java.util.List;

import ClientSide.EventsAndNews.EventPageActivity;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private List<NotificationItem> notificationList;
    private Context context;

    public NotificationAdapter(Context context, List<NotificationItem> notificationList) {
        this.context = context;
        this.notificationList = notificationList;
    }

    @NonNull
    @Override
    public NotificationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_notification, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationAdapter.ViewHolder holder, int position) {
        NotificationItem item = notificationList.get(position);
        holder.titleTextView.setText(item.getTitle());

        if (item.getImageUri() != null) {
            holder.imageView.setVisibility(View.VISIBLE);
            // Load the image using Glide from the provided content URI.
            Glide.with(context)
                    .load(Uri.parse(item.getImageUri()))
                    .into(holder.imageView);
        } else {
            holder.imageView.setVisibility(View.GONE);
        }

        // Redirect based on notification type when tapped.
        holder.itemView.setOnClickListener(v -> {
            if (item.getType().equals("Event")) {
                Intent intent = new Intent(context, EventPageActivity.class);
                context.startActivity(intent);
            } else if (item.getType().equals("News")) {
                Intent intent = new Intent(context, LocalNewsAlertsActivity.class);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView titleTextView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.notificationImageView);
            titleTextView = itemView.findViewById(R.id.notificationTitleTextView);
        }
    }
}
