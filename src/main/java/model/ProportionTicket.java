package model;

public class ProportionTicket implements Ticket{
    private String fixedVersion;
    private String component;
    private String affectedVersionDate;
    private String fixedVersionDate;
    private String openingVersion;
    private final float proportion;

    public ProportionTicket(String openingVersion){
        this.proportion = calculateProportion();
        this.openingVersion = openingVersion;
    }

    private static float calculateProportion(){
        return 0.f;
    }

}
