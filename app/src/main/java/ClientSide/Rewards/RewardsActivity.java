package ClientSide.Rewards;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ClientSide.Transaction.TransactionData;

public class RewardsActivity extends AppCompatActivity implements RewardsAdapter.OnRewardClaimListener {

    private RecyclerView rewardsRecyclerView;
    private RewardsAdapter rewardsAdapter;
    private List<Reward> rewardList;
    private int currentPoints;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rewards);

        Toolbar toolbar = findViewById(R.id.rewardsToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("ClientSide/Rewards");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Retrieve current points passed from Pointsection.
        currentPoints = getIntent().getIntExtra("CURRENT_POINTS", 0);

        rewardsRecyclerView = findViewById(R.id.rewardsRecyclerView);
        rewardsRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        // Load rewards from RewardData.
        rewardList = RewardData.getRewardList();

        rewardsAdapter = new RewardsAdapter(rewardList, currentPoints, this);
        rewardsRecyclerView.setAdapter(rewardsAdapter);
    }

    @Override
    public void onRewardClaim(Reward reward) {
        if (reward.getQuantity() <= 0) {
            Toast.makeText(this, "Out of Stock", Toast.LENGTH_SHORT).show();
            return;
        }
        if (currentPoints >= reward.getPointsRequired()) {
            currentPoints -= reward.getPointsRequired();
            reward.setQuantity(reward.getQuantity() - 1);
            Toast.makeText(this, "You claimed: " + reward.getName(), Toast.LENGTH_SHORT).show();
            rewardsAdapter.updatePoints(currentPoints);
            rewardsAdapter.notifyDataSetChanged();

            String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new Date());
            String uniqueCode = "TX-" + new SimpleDateFormat("yyyyMMddHHmmss", Locale.US).format(new Date());
            TransactionData.addTransaction("Reward Claim: " + reward.getName(), -reward.getPointsRequired(), currentDate, uniqueCode);

            Intent resultIntent = new Intent();
            resultIntent.putExtra("UPDATED_POINTS", currentPoints);
            setResult(RESULT_OK, resultIntent);
        } else {
            Toast.makeText(this, "Insufficient points for " + reward.getName(), Toast.LENGTH_SHORT).show();
        }
    }
    }

