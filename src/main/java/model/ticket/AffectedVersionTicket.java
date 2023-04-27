package model.ticket;

import model.verions.AffectedVersion;

import java.util.Date;
import java.util.List;
import java.util.Objects;

public record AffectedVersionTicket(AffectedVersion affectedVersion,
                                    List<String> fixedVersion,
                                    List<Date> fixedVersionDate,
                                    List<String> components,
                                    Date openingVersion) implements Ticket {

    @Override
    public AffectedVersion getAffectedVersion() {
        return affectedVersion;
    }

    @Override
    public List<String> getFixedVersion() {
        return fixedVersion;
    }

    @Override
    public List<String> getComponents() {
        return components;
    }

    @Override
    public List<Date> getFixedVersionDate() {
        return fixedVersionDate;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (AffectedVersionTicket) obj;
        return Objects.equals(this.affectedVersion, that.affectedVersion) &&
                Objects.equals(this.fixedVersion, that.fixedVersion) &&
                Objects.equals(this.fixedVersionDate, that.fixedVersionDate) &&
                Objects.equals(this.components, that.components) &&
                Objects.equals(this.openingVersion, that.openingVersion);
    }

    @Override
    public int hashCode() {
        return Objects.hash(affectedVersion, fixedVersion, fixedVersionDate, components, openingVersion);
    }

    @Override
    public String toString() {
        return "AffectedVersionTicket[" +
                "affectedVersion=" + affectedVersion + ", " +
                "fixedVersion=" + fixedVersion + ", " +
                "fixedVersionDate=" + fixedVersionDate + ", " +
                "components=" + components + ", " +
                "openingVersion=" + openingVersion + ']';
    }

}
