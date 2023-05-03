package control;

import exceptions.InvalidDataException;
import model.Release;
import model.Releases;
import model.ticket.Ticket;
import org.json.JSONArray;
import java.util.Date;
import java.util.List;

public class Proportion {
    private Proportion(){

    }

    //We need to compute proportion using both cold start and increment (Need to get IV for historical data)

    public static long coldStart(List<Ticket> ticketList, Releases releases){
        long propSum = 0;
        for (Ticket ticket: ticketList){
            //System.out.println(affectedVersionTicket.openingVersion() + " " + affectedVersionTicket.getAffectedVersion().getReleaseDate());
        }

        return 0L;
    }

    public static Ticket createTicket(Date openingVersionDate, Date fixedVersionDate, JSONArray injectedVersion, List<Release> versions) throws InvalidDataException {
        Date OV = openingVersionDate;
        String IV = injectedVersion.getJSONObject(0).getString("name");
        Date IVDate = null;
        Date FV = fixedVersionDate;

        for (int i = 1;i<versions.size()-1;i++) { //I don't consider first version as possible fixed version and last version as possible opening version (only resolved tickets)
            Date currentVersionDate = versions.get(i).getReleaseDate(); //Get the release date of a version
            if(openingVersionDate.before(currentVersionDate) && openingVersionDate.after(versions.get(i-1).getReleaseDate())){
                OV = (versions.get(i-1).getReleaseDate());
            }
            if(fixedVersionDate.after(currentVersionDate) && openingVersionDate.before(versions.get(i+1).getReleaseDate())){
                FV = (versions.get(i+1).getReleaseDate());
            }
            if(IV.equals(versions.get(i).getName())){
                IVDate = versions.get(i).getReleaseDate();
            }
        }

        if(IV.equals(versions.get(0).getName())){ //Check for injected version
            IVDate = versions.get(0).getReleaseDate();
        }else if(IV.equals(versions.get(versions.size()-1).getName())){
            IVDate = versions.get(versions.size()-1).getReleaseDate();
        }

        if(IVDate == null || OV.after(FV) || IVDate.after(OV) || IVDate.after(FV)){ //Don't consider FV==OV to apply smoothing
            throw new InvalidDataException();
        }
        return new Ticket(OV,FV,IVDate,IV);
    }
}
