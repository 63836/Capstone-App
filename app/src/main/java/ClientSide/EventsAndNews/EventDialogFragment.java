package ClientSide.EventsAndNews;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.example.myapplication.R;
import AdminFiles.ADminEvents.AdminEventsActivity;

public class EventDialogFragment extends DialogFragment {

    private static final String ARG_EVENT_ITEM = "event_item";

    public static EventDialogFragment newInstance(AdminEventsActivity.EventItem eventItem) {
        EventDialogFragment fragment = new EventDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_EVENT_ITEM, eventItem);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dialog_event, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            AdminEventsActivity.EventItem eventItem = (AdminEventsActivity.EventItem) getArguments().getSerializable(ARG_EVENT_ITEM);
            if (eventItem != null) {
                // Prepare data for EventFragment
                String[] eventData = new String[]{
                        eventItem.getTitle(),
                        eventItem.getDescription(),
                        eventItem.getImageUri(), // Corrected method call
                        String.valueOf(eventItem.getPointsOffered())
                };

                // Create and add EventFragment
                EventFragment eventFragment = new EventFragment(eventData);
                getChildFragmentManager().beginTransaction()
                        .replace(R.id.dialog_event_container, eventFragment)
                        .commit();
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }
}