package model.ticket;

import java.util.Date;
import java.util.List;
import java.util.Objects;

public record ProportionTicket(List<String> components,
                               Date openingVersion) implements Ticket {

    @Override
    public Date getFixedVersion() {
        return fixedVersion;
    }

    @Override
    public List<String> getComponents() {
        return components;
    }


    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (ProportionTicket) obj;
        return Objects.equals(this.fixedVersion, that.fixedVersion) &&
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
