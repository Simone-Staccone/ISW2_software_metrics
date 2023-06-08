package model;

import org.eclipse.jgit.revwalk.RevCommit;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Release{
    public int id;
    public String name;
    public Date releaseDate;
    public final List<RevCommit> allCommits = new ArrayList<>();
    public RevCommit lastCommit = null;
    public int releaseNumber;
    public List<ProjectClass> classes = new ArrayList<>();


    public Release(int id, String name, Date releaseDate, RevCommit lastCommit, int releaseNumber){
        this.id = id;
        this.name = name;
        this.releaseDate = releaseDate;
        this.lastCommit = lastCommit;
        this.releaseNumber = releaseNumber;
    }

    public void addCommit(RevCommit commit){
        this.allCommits.add(commit);
    }

    public void addProjectClass(ProjectClass projectClass){
        this.classes.add(projectClass);
    }

    public List<ProjectClass> getVersionClasses(){
        return classes;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLastCommit(RevCommit lastCommit) {
        this.lastCommit = lastCommit;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Date getReleaseDate() {
        return releaseDate;
    }

    public RevCommit getLastCommit() {
        return lastCommit;
    }

    public int getReleaseNumber() {
        return releaseNumber;
    }
}
