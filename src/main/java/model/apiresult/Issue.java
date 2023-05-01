package model.apiresult;

import control.APIParser;
import exceptions.InvalidDataException;
import model.Release;
import model.ticket.AffectedVersionTicket;
import model.ticket.ProportionTicket;
import org.json.JSONArray;

import java.text.ParseException;
import java.util.ArrayList;

import java.util.List;

public class Issue {
    public List<AffectedVersionTicket> affectedVersionTickets;
    public List<ProportionTicket> proportionTickets;

    public Issue(JSONArray issues, List<Release> releases) throws InvalidDataException {
        this.affectedVersionTickets = new ArrayList<>();
        this.proportionTickets = new ArrayList<>();
        try {
            APIParser.computeState(issues,affectedVersionTickets,proportionTickets,releases);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


    public List<AffectedVersionTicket> getAffectedVersionTickets() {
        return affectedVersionTickets;
    }


    public List<ProportionTicket> getProportionTickets() {
        return proportionTickets;
    }
}
