package model.apiresult;


import model.ticket.AffectedVersionTicket;
import model.ticket.ProportionTicket;
import org.json.JSONArray;

import java.util.List;

public interface APIResult {
    JSONArray issues = null;
    List<AffectedVersionTicket> affectedVersionTickets = null;
    List<ProportionTicket> proportionTickets = null;

    default JSONArray getIssues() {
        return issues;
    }

    default List<ProportionTicket> getProportionTickets() {
        return proportionTickets;
    }

    default List<AffectedVersionTicket> getAffectedVersionTickets() {
        return affectedVersionTickets;
    }

}
