package model.ticket;

import java.util.Date;

public class Ticket {
    private final Date openingVersionDate;
    private final Date fixedVersionDate;
    private final Date injectedVersionDate;
    private final String releaseName;



    public Ticket(Date openingVersionDate, Date fixedVersionDate, Date injectedVersionDate, String releaseName) {
        this.openingVersionDate = openingVersionDate;
        this.fixedVersionDate = fixedVersionDate;
        this.injectedVersionDate = injectedVersionDate;
        this.releaseName = releaseName;
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

    public String getReleaseName() {
        return releaseName;
    }

}
