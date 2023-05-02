package model.ticket;

import java.util.Date;

public class Ticket {
    private Date openingVersionDate;
    private Date fixedVersionDate;
    private Date injectedVersionDate;

    public Ticket(Date openingVersionDate,Date fixedVersionDate){
        this.openingVersionDate = openingVersionDate;
        this.fixedVersionDate = fixedVersionDate;
    }


    public Ticket(Date openingVersionDate,Date fixedVersionDate,Date injectedVersionDate){
        this.openingVersionDate = openingVersionDate;
        this.fixedVersionDate = fixedVersionDate;
        this.injectedVersionDate = injectedVersionDate;
    }


    public Date getOpeningVersionDate() {
        return openingVersionDate;
    }

    public Date getFixedVersionDate() {
        return fixedVersionDate;
    }

    public Date getInjectedVersionDate() {
        return injectedVersionDate;
    }
}
