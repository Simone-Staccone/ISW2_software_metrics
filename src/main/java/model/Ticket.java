package model;

import java.util.Date;
import java.util.Objects;

public record Ticket(Date openingVersionDate, Date fixedVersionDate,
                     Date injectedVersionDate, String releaseName, String key) {

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
