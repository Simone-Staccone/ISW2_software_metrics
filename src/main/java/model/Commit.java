package model;

import java.util.Date;

public record Commit(String nodeId, String author, Date date) { }
