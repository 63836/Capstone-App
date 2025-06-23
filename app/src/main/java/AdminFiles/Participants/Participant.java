package AdminFiles.Participants;

public class Participant {
    private String name;
    private String gender;
    private String contact;

    public Participant(String name, String gender, String contact) {
        this.name = name;
        this.gender = gender;
        this.contact = contact;
    }

    public String getName() { return name; }
    public String getGender() { return gender; }
    public String getContact() { return contact; }

    @Override
    public String toString() {
        return name;
    }
}
