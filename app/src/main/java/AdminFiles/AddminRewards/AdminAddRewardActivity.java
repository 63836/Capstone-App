package AdminFiles.AddminRewards;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import ClientSide.Rewards.Reward;
import ClientSide.Rewards.RewardData;

public class AdminAddRewardActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 101;

    private ImageView rewardImageView;
    private EditText rewardNameEditText, rewardQuantityEditText, rewardPointsEditText;
    private Button submitButton;
    private Uri selectedImageUri; // Holds the image URI after selection

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_reward);

        rewardImageView = findViewById(R.id.rewardImageView);
        rewardNameEditText = findViewById(R.id.rewardNameEditText);
        rewardQuantityEditText = findViewById(R.id.rewardQuantityEditText);
        rewardPointsEditText = findViewById(R.id.rewardPointsEditText);
        submitButton = findViewById(R.id.submitButton);

        // Open image picker when the image view is clicked
        rewardImageView.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        });

        submitButton.setOnClickListener(v -> {
            String name = rewardNameEditText.getText().toString().trim();
            String quantityStr = rewardQuantityEditText.getText().toString().trim();
            String pointsStr = rewardPointsEditText.getText().toString().trim();

            if (name.isEmpty() || quantityStr.isEmpty() || pointsStr.isEmpty() || selectedImageUri == null) {
                Toast.makeText(this, "Please fill all fields and select an image", Toast.LENGTH_SHORT).show();
                return;
            }
            int quantity = Integer.parseInt(quantityStr);
            int points = Integer.parseInt(pointsStr);

            // Create a new Reward object using the admin-added details.
            Reward newReward = new Reward(name, points, quantity, selectedImageUri.toString());
            // Add the reward to our shared data source.
            RewardData.addReward(newReward);
            Toast.makeText(this, "Reward added successfully", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            rewardImageView.setImageURI(selectedImageUri);
        }
    }
}
