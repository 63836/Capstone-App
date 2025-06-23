package ClientSide.Rewards;

import java.util.ArrayList;
import java.util.List;

public class RewardData {
    private static List<Reward> rewardList = new ArrayList<>();

    public static List<Reward> getRewardList() {
        if (rewardList.isEmpty()) {
            // Add default rewards if the list is empty.
            // Here we use built-in Android icons as defaults.
            rewardList.add(new Reward("Rice", 50, android.R.drawable.ic_menu_gallery));
            rewardList.add(new Reward("Canned Goods", 70, android.R.drawable.ic_menu_gallery));
        }
        return rewardList;
    }

    public static void addReward(Reward reward) {
        rewardList.add(reward);
    }
}
