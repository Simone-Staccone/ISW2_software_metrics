package model;

import java.util.Date;

public record Commit(String nodeId, String author, Date date) {

    public String getNodeId() {
        return nodeId;
    }

    public String getAuthor() {
        return author;
    }

    public Date getDate() {
        return date;
    }
}
