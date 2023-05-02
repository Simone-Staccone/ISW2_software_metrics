package model;

import utils.DateParser;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Releases {
    private final List<Release> releaseList = new ArrayList<>();
    private Release latestRelease;

    public Releases(List<List<String>> entries){
        Date latestReleaseDate = DateParser.parseStringToDate(entries.get(0).get(2));  //Initialize the last date to the first of the list
        for (List<String> entry:entries) {
            Release release = new Release(Integer.parseInt(entry.get(0)),
                    entry.get(1),
                    DateParser.parseStringToDate(entry.get(2)),
                    null);
            releaseList.add(release);
            if(latestReleaseDate.before(release.getReleaseDate())){
                latestRelease = release;
                latestReleaseDate = release.getReleaseDate();
            }
        }
    }

    public Release getLatestRelease() {
        return latestRelease;
    }

    public List<Release> getReleaseList() {
        return releaseList;
    }
}
