package control;

import model.Release;
import model.Releases;
import model.ticket.AffectedVersionTicket;
import model.ticket.Ticket;
import org.json.JSONArray;
import utils.DateParser;

import java.util.Date;
import java.util.List;

public class Proportion {
    private Proportion(){

    }

    //We need to compute proportion using both cold start and increment (Need to get IV for historical data)

    public static long coldStart(List<AffectedVersionTicket> affectedVersionTickets, Releases infos){
        long propSum = 0;
        for (AffectedVersionTicket affectedVersionTicket: affectedVersionTickets){
            //System.out.println(affectedVersionTicket.openingVersion() + " " + affectedVersionTicket.getAffectedVersion().getReleaseDate());
        }

        return 0L;
    }

    public static Ticket createTicket(Date openingVersionDate, Date fixedVersionDate, JSONArray injectedVersion, List<Release> versions) {
        Date OV = openingVersionDate;
        Date IV;
        Date FV = fixedVersionDate;
        boolean foundOV = false;
        boolean foundFV = false;

        for (int i = 0;i<versions.size();i++) {
            Date currentVersionDate = versions.get(i).getReleaseDate(); //Get the release date of a version
            if(openingVersionDate.before(currentVersionDate) && !foundOV){
                OV = currentVersionDate; //Get the date of the release which is before the opening version date
            }else{
                foundOV = true;
            }
            if(fixedVersionDate.before(currentVersionDate) && !foundFV && i+1 < versions.size()){
                FV = versions.get(i+1).getReleaseDate(); //Get the date of the release which is after the fixed version date
            }else{
                foundFV = true;
            }
        }

        System.out.println(versions);
        System.out.println(OV + " " + FV);
        System.out.println(openingVersionDate + " " + fixedVersionDate);
        return null;
    }
}
