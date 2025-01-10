package com.example.myapplication;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import android.widget.ImageView;
import android.content.res.ColorStateList;
import androidx.core.content.ContextCompat;

public class EventFragment extends Fragment {

    private static final String ARG_TITLE = "title";
    private static final String ARG_DESCRIPTION = "description";
    private static final String ARG_ITEM_NAME = "item_name";
    private static final String ARG_POINTS_REQUIRED = "points_required";

    private String title;
    private String description;
    private String itemName;
    private int pointsRequired;

    private OnItemClaimListener itemClaimListener;

    public interface OnItemClaimListener {
        void onItemClaimed(String itemName, int pointsRequired);
    }

    public EventFragment() {
        // Required empty public constructor
    }

    public static EventFragment newInstance(String title, String description, String itemName, int pointsRequired) {
        EventFragment fragment = new EventFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        args.putString(ARG_DESCRIPTION, description);
        args.putString(ARG_ITEM_NAME, itemName);
        args.putInt(ARG_POINTS_REQUIRED, pointsRequired);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            title = getArguments().getString(ARG_TITLE);
            description = getArguments().getString(ARG_DESCRIPTION);
            itemName = getArguments().getString(ARG_ITEM_NAME);
            pointsRequired = getArguments().getInt(ARG_POINTS_REQUIRED);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event, container, false);

        TextView titleTextView = view.findViewById(R.id.eventTitle);
        TextView descriptionTextView = view.findViewById(R.id.eventDescription);
        TextView itemNameTextView = view.findViewById(R.id.itemName);
        TextView pointsRequiredTextView = view.findViewById(R.id.pointsRequired);
        MaterialButton claimButton = view.findViewById(R.id.claimButton);

        titleTextView.setText(title);
        descriptionTextView.setText(description);
        itemNameTextView.setText(itemName);
        pointsRequiredTextView.setText(pointsRequired + " Points");

        claimButton.setOnClickListener(v -> {
            if (itemClaimListener != null) {
                itemClaimListener.onItemClaimed(itemName, pointsRequired);
            }
        });

        setupEventTheme(view);

        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnItemClaimListener) {
            itemClaimListener = (OnItemClaimListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnItemClaimListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        itemClaimListener = null;
    }

    private void setupEventTheme(View view) {
        ImageView backgroundImage = view.findViewById(R.id.eventBackgroundImage);
        ImageView eventIcon = view.findViewById(R.id.eventIcon);
        MaterialButton claimButton = view.findViewById(R.id.claimButton);

        // Set unique background and theme based on event title
        switch (title.toLowerCase()) {
            case "community clean-up":
                backgroundImage.setImageResource(R.drawable.cleanup_background);
                eventIcon.setImageResource(R.drawable.ic_cleanup);
                claimButton.setBackgroundTintList(ColorStateList.valueOf(
                        ContextCompat.getColor(requireContext(), R.color.cleanup_color)));
                break;
            case "local food festival":
                backgroundImage.setImageResource(R.drawable.food_background);
                eventIcon.setImageResource(R.drawable.ic_food);
                claimButton.setBackgroundTintList(ColorStateList.valueOf(
                        ContextCompat.getColor(requireContext(), R.color.food_color)));
                break;
            case "fitness challenge":
                backgroundImage.setImageResource(R.drawable.fitness_background);
                eventIcon.setImageResource(R.drawable.ic_fitness);
                claimButton.setBackgroundTintList(ColorStateList.valueOf(
                        ContextCompat.getColor(requireContext(), R.color.fitness_color)));
                break;
            case "art workshop":
                backgroundImage.setImageResource(R.drawable.art_background);
                eventIcon.setImageResource(R.drawable.ic_art);
                claimButton.setBackgroundTintList(ColorStateList.valueOf(
                        ContextCompat.getColor(requireContext(), R.color.art_color)));
                break;
            case "tech seminar":
                backgroundImage.setImageResource(R.drawable.tech_background);
                eventIcon.setImageResource(R.drawable.ic_tech);
                claimButton.setBackgroundTintList(ColorStateList.valueOf(
                        ContextCompat.getColor(requireContext(), R.color.tech_color)));
                break;
            default:
                // Set a default background and icon if the event title doesn't match any case
                backgroundImage.setImageResource(R.drawable.default_background);
                eventIcon.setImageResource(R.drawable.ic_default);
                claimButton.setBackgroundTintList(ColorStateList.valueOf(
                        ContextCompat.getColor(requireContext(), R.color.default_color)));
                break;
        }
    }
}

