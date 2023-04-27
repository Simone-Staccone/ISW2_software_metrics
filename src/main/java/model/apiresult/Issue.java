package model.apiresult;

import control.APIParser;
import exceptions.InvalidDataException;
import model.ticket.AffectedVersionTicket;
import model.ticket.ProportionTicket;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

public class Issue implements APIResult {
    public List<AffectedVersionTicket> affectedVersionTickets;
    public List<ProportionTicket> proportionTickets;

    public Issue(JSONArray issues) throws InvalidDataException {
        this.affectedVersionTickets = new ArrayList<>();
        this.proportionTickets = new ArrayList<>();
        APIParser.computeState(issues,affectedVersionTickets,proportionTickets);
    }

    @Override
    public List<AffectedVersionTicket> getAffectedVersionTickets() {
        return affectedVersionTickets;
    }

    @Override
    public List<ProportionTicket> getProportionTickets() {
        return proportionTickets;
    }
}
