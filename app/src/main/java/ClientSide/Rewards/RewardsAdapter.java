package ClientSide.Rewards;

import android.net.Uri;
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

public class RewardsAdapter extends RecyclerView.Adapter<RewardsAdapter.RewardViewHolder> {

    private List<Reward> rewards;
    private int currentPoints;
    private OnRewardClaimListener listener;

    public interface OnRewardClaimListener {
        void onRewardClaim(Reward reward);
    }

    public RewardsAdapter(List<Reward> rewards, int currentPoints, OnRewardClaimListener listener) {
        this.rewards = rewards;
        this.currentPoints = currentPoints;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RewardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reward, parent, false);
        return new RewardViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RewardViewHolder holder, int position) {
        Reward reward = rewards.get(position);
        holder.rewardName.setText(reward.getName());
        holder.rewardPoints.setText("Points: " + reward.getPointsRequired());
        holder.rewardQuantityText.setText("Available: " + reward.getQuantity());

        if (reward.getQuantity() <= 0) {
            holder.claimButton.setText("Out of Stock");
            holder.claimButton.setEnabled(false);
        } else {
            holder.claimButton.setText("Claim");
            holder.claimButton.setEnabled(true);
        }

        // Load the image: if a custom image URI is provided, load it and clear tint; otherwise, load the default image.
        if (reward.getImageUri() != null && !reward.getImageUri().isEmpty()) {
            try {
                holder.rewardImage.setImageURI(Uri.parse(reward.getImageUri()));
                // Clear any tint so that the custom image displays correctly.
                holder.rewardImage.clearColorFilter();
            } catch (Exception e) {
                holder.rewardImage.setImageResource(reward.getImageResource());
            }
        } else {
            holder.rewardImage.setImageResource(reward.getImageResource());
        }

        holder.claimButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onRewardClaim(reward);
            }
        });
    }

    @Override
    public int getItemCount() {
        return rewards.size();
    }

    public void updatePoints(int newPoints) {
        this.currentPoints = newPoints;
        notifyDataSetChanged();
    }

    static class RewardViewHolder extends RecyclerView.ViewHolder {
        ImageView rewardImage;
        TextView rewardName;
        TextView rewardPoints;
        TextView rewardQuantityText;
        Button claimButton;

        public RewardViewHolder(@NonNull View itemView) {
            super(itemView);
            rewardImage = itemView.findViewById(R.id.rewardImage);
            rewardName = itemView.findViewById(R.id.rewardName);
            rewardPoints = itemView.findViewById(R.id.rewardPoints);
            rewardQuantityText = itemView.findViewById(R.id.rewardQuantityText);
            claimButton = itemView.findViewById(R.id.claimButton);
        }
    }
}
