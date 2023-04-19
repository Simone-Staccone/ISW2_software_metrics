import java.util.Date;

public class Ticket {
    private String affectedVersion = "";
    private String fixedVersion = "";
    private String component = "";
    private String affectedVersionDate = "";
    private String fixedVersionDate = "";

    public Ticket(){
    }

    public Ticket(String affectedVersion, String fixedVersion){
        this.affectedVersion = affectedVersion;
        this.fixedVersion = fixedVersion;
    }

    public Ticket(String affectedVersion, String fixedVersion,String component){
        this.affectedVersion = affectedVersion;
        this.fixedVersion = fixedVersion;
        this.component = component;
    }

    public Ticket(String affectedVersion, String fixedVersion,String component, String affectedVersionDate, String fixedVersionDate){
        this.affectedVersion = affectedVersion;
        this.fixedVersion = fixedVersion;
        this.component = component;
        this.affectedVersionDate = affectedVersionDate;
        this.fixedVersionDate = fixedVersionDate;
    }



    public String getAffectedVersion() {
        return affectedVersion;
    }

    public String getFixedVersion() {
        return fixedVersion;
    }

    public String getComponent() {
        return component;
    }

    public String getAffectedVersionDate() {
        return affectedVersionDate;
    }

    public String getFixedVersionDate() {
        return fixedVersionDate;
    }
}
