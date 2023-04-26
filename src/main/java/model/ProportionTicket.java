package model;

public class ProportionTicket implements Ticket{
    private final String openingVersion;
    private final float proportion;

    public ProportionTicket(String openingVersion){
        this.proportion = calculateProportion();
        this.openingVersion = openingVersion;
    }

    private static float calculateProportion(){
        return 0.f;
    }

}
