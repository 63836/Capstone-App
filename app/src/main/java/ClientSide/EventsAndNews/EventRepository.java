package ClientSide.EventsAndNews;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import AdminFiles.Participants.Participant;

public class EventRepository {
    private static Map<String, List<Participant>> eventRegistrations = new HashMap<>();

    public static void addRegistration(String eventTitle, Participant participant) {
        if (!eventRegistrations.containsKey(eventTitle)) {
            eventRegistrations.put(eventTitle, new ArrayList<>());
        }
        eventRegistrations.get(eventTitle).add(participant);
    }

    public static List<Participant> getRegistrations(String eventTitle) {
        return eventRegistrations.getOrDefault(eventTitle, new ArrayList<>());
    }
}
