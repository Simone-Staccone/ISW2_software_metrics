package model;

import java.util.Date;
import java.util.Objects;

public final class Ticket {
    private final Date openingVersionDate;
    private final Date fixedVersionDate;
    private final Date injectedVersionDate;
    private final String releaseName;
    private final String key;

    public Ticket(Date openingVersionDate, Date fixedVersionDate,
           Date injectedVersionDate, String releaseName, String key) {
        this.openingVersionDate = openingVersionDate;
        this.fixedVersionDate = fixedVersionDate;
        this.injectedVersionDate = injectedVersionDate;
        this.releaseName = releaseName;
        this.key = key;
    }

    public Date openingVersionDate() {
        return openingVersionDate;
    }

    public Date fixedVersionDate() {
        return fixedVersionDate;
    }

    public Date injectedVersionDate() {
        return injectedVersionDate;
    }

    public String releaseName() {
        return releaseName;
    }

    public String key() {
        return key;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Ticket) obj;
        return Objects.equals(this.openingVersionDate, that.openingVersionDate) &&
                Objects.equals(this.fixedVersionDate, that.fixedVersionDate) &&
                Objects.equals(this.injectedVersionDate, that.injectedVersionDate) &&
                Objects.equals(this.releaseName, that.releaseName) &&
                Objects.equals(this.key, that.key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(openingVersionDate, fixedVersionDate, injectedVersionDate, releaseName, key);
    }

    @Override
    public String toString() {
        return "Ticket[" +
                "openingVersionDate=" + openingVersionDate + ", " +
                "fixedVersionDate=" + fixedVersionDate + ", " +
                "injectedVersionDate=" + injectedVersionDate + ", " +
                "releaseName=" + releaseName + ", " +
                "key=" + key + ']';
    }

}
