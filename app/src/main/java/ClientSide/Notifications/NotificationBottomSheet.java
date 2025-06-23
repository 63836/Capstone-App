package ClientSide.Notifications;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import java.util.List;

public class NotificationBottomSheet extends BottomSheetDialogFragment {

    private RecyclerView notificationRecyclerView;
    private NotificationAdapter adapter;

    public NotificationBottomSheet() { }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_notification, container, false);
        notificationRecyclerView = view.findViewById(R.id.notificationRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        notificationRecyclerView.setLayoutManager(layoutManager);
        List<NotificationItem> list = NotificationCenter.notificationList;
        adapter = new NotificationAdapter(getContext(), list);
        notificationRecyclerView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }
}
