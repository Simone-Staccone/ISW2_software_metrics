package model;

import org.eclipse.jgit.revwalk.RevCommit;

import java.util.ArrayList;
import java.util.List;

public class ProjectClass {
    private final String name;
    private final String content;
    private final int release;
    private final List<RevCommit> commits = new ArrayList<>();    //These are the commits of the specified release that have modified the class

    private int loc;
    private int nr;
    private int nAuth;
    private int locAdded;
    private int maxLocAdded;
    private double avgLocAdded;
    private int churn;
    private int maxChurn;
    private double avgChurn;

    private final List<Integer> addedLinesList;
    private final List<Integer> deletedLinesList;
    private int deletedLoc;
    private int fanOut;
    private int methodsNumber;

    public ProjectClass(int release, String name, String content) {
        this.name = name;
        this.content = content;
        this.release = release;
        this.loc = 0;
        this.nAuth = 0;

        this.nr = 0;
        this.locAdded = 0;
        this.maxLocAdded = 0;
        this.avgLocAdded = 0;
        this.churn = 0;
        this.maxChurn = 0;
        this.avgChurn = 0;
        this.fanOut = 0;
        this.methodsNumber = 0;

        this.addedLinesList = new ArrayList<>();
        this.deletedLinesList = new ArrayList<>();

    }


    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the content
     */
    public String getContent() {
        return content;
    }

    /**
     * @return the release
     */
    public int getRelease() {
        return release;
    }

    /**
     * @return the commits
     */
    public List<RevCommit> getCommits() {
        return commits;
    }

    /**
     * @return the size
     */
    public int getLoc() {
        return loc;
    }

    /**
     * @param loc the size to set
     */
    public void setLoc(int loc) {
        this.loc = loc;
    }

    /**
     * @return the nr
     */
    public int getNr() {
        return nr;
    }

    /**
     * @param nr the nr to set
     */
    public void setNr(int nr) {
        this.nr = nr;
    }

    /**
     * @return the nAuth
     */
    public int getnAuth() {
        return nAuth;
    }

    /**
     * @param nAuth the nAuth to set
     */
    public void setnAuth(int nAuth) {
        this.nAuth = nAuth;
    }

    /**
     * @return the locAdded
     */
    public int getLocAdded() {
        return locAdded;
    }

    /**
     * @param locAdded the locAdded to set
     */
    public void setLocAdded(int locAdded) {
        this.locAdded = locAdded;
    }

    /**
     * @return the maxLocAdded
     */
    public int getMaxLocAdded() {
        return maxLocAdded;
    }

    /**
     * @param maxLocAdded the maxLocAdded to set
     */
    public void setMaxLocAdded(int maxLocAdded) {
        this.maxLocAdded = maxLocAdded;
    }

    /**
     * @return the avgLocAdded
     */
    public double getAvgLocAdded() {
        return avgLocAdded;
    }

    /**
     * @param avgLocAdded the avgLocAdded to set
     */
    public void setAvgLocAdded(double avgLocAdded) {
        this.avgLocAdded = avgLocAdded;
    }

    /**
     * @return the churn
     */
    public int getChurn() {
        return churn;
    }

    /**
     * @param churn the churn to set
     */
    public void setChurn(int churn) {
        this.churn = churn;
    }

    /**
     * @return the maxChurn
     */
    public int getMaxChurn() {
        return maxChurn;
    }

    /**
     * @param maxChurn the maxChurn to set
     */
    public void setMaxChurn(int maxChurn) {
        this.maxChurn = maxChurn;
    }

    /**
     * @return the avgChurn
     */
    public double getAvgChurn() {
        return avgChurn;
    }

    /**
     * @param avgChurn the avgChurn to set
     */
    public void setAvgChurn(double avgChurn) {
        this.avgChurn = avgChurn;
    }

    /**
     * @return the addedLinesList
     */
    public List<Integer> getAddedLinesList() {
        return addedLinesList;
    }

    /**
     * @return the deletedLinesList
     */
    public List<Integer> getDeletedLinesList() {
        return deletedLinesList;
    }

    public void addCommit(RevCommit commit) {
        this.commits.add(commit);
    }

    public void setDeletedLoc(int linesDeleted) {
        this.deletedLoc = linesDeleted;
    }


    public int getLocDeleted() {
        return this.deletedLoc;
    }

    public int getFanOut() {
        return fanOut;
    }

    public void setFanOut(int fanOut) {
        this.fanOut = fanOut;
    }

    public void setMethodsNumber(int methodsNumber) {
        this.methodsNumber = methodsNumber;
    }

    public int getMethodNumber() {
        return this.methodsNumber;
    }
}
