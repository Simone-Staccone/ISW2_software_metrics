package model;

import java.util.Date;

public record Ticket(Date openingVersionDate, Date fixedVersionDate,
                     Date injectedVersionDate, String releaseName, String key) {
}
