package model.ticket;

import model.verions.AffectedVersion;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public interface Ticket {
    String openingVersion = "";
    List<String> components = new ArrayList<>();
    List<String> fixedVersion = new ArrayList<>();
    List<Date> fixedVersionDate = new ArrayList<>();
    AffectedVersion affectedVersion = null;

    default List<String> getFixedVersion() {
        return fixedVersion;
    }
    default List<Date> getFixedVersionDate() {
        return fixedVersionDate;
    }
    default String getOpeningVersion() {
        return openingVersion;
    }
    default List<String> getComponents(){return components;}
    default AffectedVersion getAffectedVersion(){return affectedVersion;}
}
