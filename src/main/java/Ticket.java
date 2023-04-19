public class Ticket {
    private String affectedVersion;

    public Ticket(String affectedVersion){
        this.affectedVersion = affectedVersion;
    }

    public String getAffectedVersion() {
        return affectedVersion;
    }
}
