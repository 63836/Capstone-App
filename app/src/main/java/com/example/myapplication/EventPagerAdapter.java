package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class EventPagerAdapter extends FragmentStateAdapter {

    private static final String[][] EVENT_DATA = {
            {"Community Clean-up", "Join us for a community clean-up event! Claim your cleaning kit.", "Cleaning Kit", "300"},
            {"Local Food Festival", "Experience local cuisines! Claim your food voucher.", "Food Voucher", "400"},
            {"Fitness Challenge", "Participate in our fitness challenge! Claim your sports water bottle.", "Sports Water Bottle", "350"},
            {"Art Workshop", "Unleash your creativity! Claim your art supplies kit.", "Art Supplies Kit", "500"},
            {"Tech Seminar", "Learn about the latest tech trends! Claim your e-book bundle.", "E-book Bundle", "450"}
    };

    public EventPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        String[] eventInfo = EVENT_DATA[position];
        return EventFragment.newInstance(eventInfo[0], eventInfo[1], eventInfo[2], Integer.parseInt(eventInfo[3]));
    }

    @Override
    public int getItemCount() {
        return EVENT_DATA.length;
    }
}

