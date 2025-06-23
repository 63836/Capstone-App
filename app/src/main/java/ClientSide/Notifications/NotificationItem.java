package ClientSide.Notifications;

public class NotificationItem {
    private String title;
    private String type;
    private String imageUri;

    public NotificationItem(String title, String type, String imageUri) {
        this.title = title;
        this.type = type;
        this.imageUri = imageUri;
    }

    // Add these getter methods:
    public String getTitle() {
        return title;
    }

    public String getType() {
        return type;
    }

    public String getImageUri() {
        return imageUri;
    }
}
