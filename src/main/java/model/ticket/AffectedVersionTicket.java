package model.ticket;

import model.verions.AffectedVersion;

import java.util.Date;
import java.util.List;
import java.util.Objects;

public record AffectedVersionTicket(AffectedVersion affectedVersion,
                                    List<String> components,
                                    Date openingVersion,
                                    Date fixedVersion) {

    public AffectedVersion getAffectedVersion() {
        return affectedVersion;
    }


    public List<String> getComponents() {
        return components;
    }


    public Date getFixedVersion() {
        return fixedVersion;
    }


    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (AffectedVersionTicket) obj;
        return Objects.equals(this.affectedVersion, that.affectedVersion) &&
                Objects.equals(this.components, that.components) &&
                Objects.equals(this.openingVersion, that.openingVersion);
    }

    @Override
    public int hashCode() {
        return Objects.hash(affectedVersion, fixedVersion, components, openingVersion);
    }

    @Override
    public String toString() {
        return "AffectedVersionTicket[" +
                "affectedVersion=" + affectedVersion + ", " +
                "fixedVersion=" + fixedVersion + ", " +
                "components=" + components + ", " +
                "openingVersion=" + openingVersion + ']';
    }

}
