package model.verions;

import java.util.Date;

public record AffectedVersion(int id, String name, Date releaseDate){

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Date getReleaseDate() {
        return releaseDate;
    }
}
