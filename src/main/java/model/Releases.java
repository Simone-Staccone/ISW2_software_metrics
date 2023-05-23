package model;

import utils.DateParser;

import java.util.ArrayList;
import java.util.List;

public class Releases {
    private final List<Release> releaseList = new ArrayList<>();


    public Releases(List<List<String>> entries){
        for (List<String> entry:entries) {
            Release release = new Release(Integer.parseInt(entry.get(0)),
                    entry.get(1),
                    DateParser.parseStringToDate(entry.get(2)),
                    null,
                    Integer.parseInt(entry.get(3)) + 1);
            releaseList.add(release);
        }
    }

    public List<Release> getReleaseList() {
        return releaseList;
    }
}
