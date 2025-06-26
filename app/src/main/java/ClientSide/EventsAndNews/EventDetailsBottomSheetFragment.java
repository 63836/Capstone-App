package ClientSide.EventsAndNews;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.example.myapplication.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import AdminFiles.ADminEvents.AdminEventsActivity;

public class EventDetailsBottomSheetFragment extends BottomSheetDialogFragment {

    private AdminEventsActivity.EventItem eventItem;

    public static EventDetailsBottomSheetFragment newInstance(AdminEventsActivity.EventItem eventItem) {
        EventDetailsBottomSheetFragment fragment = new EventDetailsBottomSheetFragment();
        Bundle args = new Bundle();
        args.putSerializable("eventItem", eventItem);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            eventItem = (AdminEventsActivity.EventItem) getArguments().getSerializable("eventItem");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_event_details, container, false);

        TextView titleTextView = view.findViewById(R.id.eventTitleTextView);
        TextView descriptionTextView = view.findViewById(R.id.eventDescriptionTextView);
        TextView pointsTextView = view.findViewById(R.id.eventPointsTextView);
        Button registerButton = view.findViewById(R.id.registerButton);

        if (eventItem != null) {
            titleTextView.setText(eventItem.getTitle());
            descriptionTextView.setText(eventItem.getDescription());
            pointsTextView.setText("Points: " + eventItem.getPointsOffered());

            registerButton.setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), EventRegistrationActivity.class);
                intent.putExtra("EVENT_TITLE", eventItem.getTitle());
                startActivity(intent);
                dismiss();
            });
        }

        return view;
    }
}