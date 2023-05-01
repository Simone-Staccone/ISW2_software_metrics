package model.ticket;

import model.verions.AffectedVersion;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public interface Ticket {
    String openingVersion = "";
    List<String> components = new ArrayList<>();
    Date fixedVersion = new Date();
    AffectedVersion affectedVersion = null;

    default Date getFixedVersion() {
        return fixedVersion;
    }
    default String getOpeningVersion() {
        return openingVersion;
    }
    default List<String> getComponents(){return components;}
    default AffectedVersion getAffectedVersion(){return affectedVersion;}
}
