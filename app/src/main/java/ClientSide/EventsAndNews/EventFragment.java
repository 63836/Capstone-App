package ClientSide.EventsAndNews;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;
import com.example.myapplication.R;

public class EventFragment extends Fragment {
    private String[] eventData;

    // Constructor to receive event data
    public EventFragment(String[] eventData) {
        this.eventData = eventData;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment (ensure fragment_event.xml supports points offered display)
        View view = inflater.inflate(R.layout.fragment_event, container, false);

        ImageView eventBackgroundImage = view.findViewById(R.id.eventBackgroundImage);
        TextView eventTitle = view.findViewById(R.id.eventTitle);
        TextView eventDescription = view.findViewById(R.id.eventDescription);
        TextView eventPointsOffered = view.findViewById(R.id.eventPointsOffered);

        eventTitle.setText(eventData[0]);
        eventDescription.setText(eventData[1]);
        eventPointsOffered.setText("Points Offered: " + eventData[3]);

        if (eventData.length > 2 && eventData[2] != null && !eventData[2].isEmpty()) {
            Glide.with(this)
                    .load(Uri.parse(eventData[2]))
                    .into(eventBackgroundImage);
        } else {
            eventBackgroundImage.setImageResource(R.drawable.default_background);
        }

        // Change the button behavior from "Claim Reward" to "Register"
        view.findViewById(R.id.claimButton).setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), EventRegistrationActivity.class);
            intent.putExtra("eventTitle", eventData[0]);
            startActivity(intent);
        });

        return view;
    }
}
