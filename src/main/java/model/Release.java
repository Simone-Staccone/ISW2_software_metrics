package model;

import java.util.Date;

public record Release(int id, String name, Date releaseDate) {

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
