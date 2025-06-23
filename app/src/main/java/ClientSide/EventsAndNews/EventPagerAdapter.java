package ClientSide.EventsAndNews;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class EventPagerAdapter extends FragmentStateAdapter {
    private String[][] eventData;

    public EventPagerAdapter(FragmentActivity fa, String[][] eventData) {
        super(fa);
        this.eventData = eventData;
    }

    @Override
    public Fragment createFragment(int position) {
        return new EventFragment(eventData[position]);
    }

    @Override
    public int getItemCount() {
        return eventData.length;
    }
}
