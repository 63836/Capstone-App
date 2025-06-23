package ClientSide.Rewards;

public class Reward {
    private String name;
    private int pointsRequired;
    private int quantity;
    private int imageResource;   // For default rewards
    private String imageUri;     // For admin-added rewards (custom image)

    // Constructor for default rewards – default quantity is set to 1
    public Reward(String name, int pointsRequired, int imageResource) {
        this.name = name;
        this.pointsRequired = pointsRequired;
        this.imageResource = imageResource;
        this.quantity = 1;
        this.imageUri = null;
    }

    // Constructor for admin-added rewards
    public Reward(String name, int pointsRequired, int quantity, String imageUri) {
        this.name = name;
        this.pointsRequired = pointsRequired;
        this.quantity = quantity;
        this.imageUri = imageUri;
        this.imageResource = 0;
    }

    public String getName() {
        return name;
    }

    public int getPointsRequired() {
        return pointsRequired;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getImageResource() {
        return imageResource;
    }

    public String getImageUri() {
        return imageUri;
    }
}
