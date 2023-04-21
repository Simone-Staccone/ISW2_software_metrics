package model;

public class AffectedVersionTicket implements Ticket{
    private final String affectedVersion;
    private final String fixedVersion;
    private String component;
    private String affectedVersionDate;
    private String fixedVersionDate;

    public AffectedVersionTicket(String affectedVersion, String fixedVersion){
        this.affectedVersion = affectedVersion;
        this.fixedVersion = fixedVersion;
    }

    public AffectedVersionTicket(String affectedVersion, String fixedVersion, String component){
        this.affectedVersion = affectedVersion;
        this.fixedVersion = fixedVersion;
        this.component = component;
    }

    public AffectedVersionTicket(String affectedVersion, String fixedVersion, String component, String affectedVersionDate, String fixedVersionDate){
        this.affectedVersion = affectedVersion;
        this.fixedVersion = fixedVersion;
        this.component = component;
        this.affectedVersionDate = affectedVersionDate;
        this.fixedVersionDate = fixedVersionDate;
    }

    public AffectedVersionTicket(String affectedVersion, String fixedVersion, String component, String affectedVersionDate, String fixedVersionDate, String releaseDate){
        this.affectedVersion = affectedVersion;
        this.fixedVersion = fixedVersion;
        this.component = component;
        this.affectedVersionDate = affectedVersionDate;
        this.fixedVersionDate = fixedVersionDate;
    }

    public String getAffectedVersion() {
        return affectedVersion;
    }

    public String getAffectedVersionDate() {
        return affectedVersionDate;
    }



}
