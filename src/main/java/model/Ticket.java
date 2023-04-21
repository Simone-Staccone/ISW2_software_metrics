package model;

public interface Ticket {
    String fixedVersion = "";
    String component = "";
    String fixedVersionDate = "";
    String releaseDate = "";
    String openingVersion = "";

    default String getReleaseDate() {
        return releaseDate;
    }
    default String getFixedVersion() {
        return fixedVersion;
    }
    default String getComponent() {
        return component;
    }
    default String getFixedVersionDate() {
        return fixedVersionDate;
    }
    default String getOpeningVersion() {
        return openingVersion;
    }

}
