package ClientSide.EventsAndNews;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import AdminFiles.ADminEvents.AdminEventsActivity;
import AdminFiles.Participants.Participant;

public class EventRepository {

    // Centralized static list for events
    public static final List<AdminEventsActivity.EventItem> eventList = new ArrayList<>();
    // Centralized static map for event registrations
    private static final Map<String, List<Participant>> eventRegistrations = new HashMap<>();

    // Static initializer to populate the list
    static {
        eventList.add(new AdminEventsActivity.EventItem("Community Clean-up", "Join us for a community clean-up event", "", 300, 16.4110, 120.5965));
        eventList.add(new AdminEventsActivity.EventItem("Charity Run", "Participate in a charity run for a good cause", "", 500, 16.4130, 120.5980));
        eventList.add(new AdminEventsActivity.EventItem("Book Drive", "Donate books for the local library", "", 100, 16.4095, 120.5940));
        eventList.add(new AdminEventsActivity.EventItem("Tree Planting", "Help us make our community greener", "", 200, 16.4150, 120.6000));
    }

    public static List<AdminEventsActivity.EventItem> getEvents() {
        return eventList;
    }

    public static void addEvent(AdminEventsActivity.EventItem event) {
        eventList.add(event);
    }

    /**
     * Adds a participant to the registration list for a specific event.
     *
     * @param eventTitle The title of the event.
     * @param participant The participant to register.
     */
    public static void addRegistration(String eventTitle, Participant participant) {
        if (!eventRegistrations.containsKey(eventTitle)) {
            eventRegistrations.put(eventTitle, new ArrayList<>());
        }
        eventRegistrations.get(eventTitle).add(participant);
    }

    /**
     * Retrieves the list of registered participants for a specific event.
     *
     * @param eventTitle The title of the event.
     * @return A list of participants registered for the event.
     */
    public static List<Participant> getRegistrations(String eventTitle) {
        return eventRegistrations.getOrDefault(eventTitle, new ArrayList<>());
    }
}