package AdminFiles.ADminEvents;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.R;
import java.util.List;

public class AdminEventAdapter extends RecyclerView.Adapter<AdminEventAdapter.EventViewHolder> {

    private List<AdminEventsActivity.EventItem> eventList;
    private OnEventClickListener onEventClickListener;

    public interface OnEventClickListener {
        void onEventClick(AdminEventsActivity.EventItem eventItem);
    }

    public AdminEventAdapter(List<AdminEventsActivity.EventItem> eventList, OnEventClickListener onEventClickListener) {
        this.eventList = eventList;
        this.onEventClickListener = onEventClickListener;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_event, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        AdminEventsActivity.EventItem eventItem = eventList.get(position);
        holder.bind(eventItem, onEventClickListener);
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView descriptionTextView;
        TextView pointsTextView;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.eventTitleTextView);
            descriptionTextView = itemView.findViewById(R.id.eventDescriptionTextView);
            pointsTextView = itemView.findViewById(R.id.eventPointsTextView);
        }

        public void bind(final AdminEventsActivity.EventItem eventItem, final OnEventClickListener listener) {
            titleTextView.setText(eventItem.getTitle());
            descriptionTextView.setText(eventItem.getDescription());
            pointsTextView.setText("Points: " + eventItem.getPointsOffered());
            itemView.setOnClickListener(v -> listener.onEventClick(eventItem));
        }
    }
}