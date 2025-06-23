package ClientSide.Notifications;

import java.util.ArrayList;
import java.util.List;

public class NotificationCenter {
    public static List<NotificationItem> notificationList = new ArrayList<>();

    public static void addNotification(NotificationItem item) {
        notificationList.add(item);
    }
}