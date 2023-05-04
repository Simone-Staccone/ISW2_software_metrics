package model;

import org.eclipse.jgit.revwalk.RevCommit;

import java.util.Date;

public record Release(int id, String name, Date releaseDate, RevCommit commit, int releaseNumber) {

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Date getReleaseDate() {
        return releaseDate;
    }

    public RevCommit getLastCommit(){ //Each release has a unique commit associated which is the last commit before the release
        return commit;
    }

    public int getReleaseNumber(){return releaseNumber;}
}
