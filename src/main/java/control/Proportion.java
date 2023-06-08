package control;

import exceptions.InvalidDataException;
import model.Release;
import model.Releases;
import model.Ticket;
import org.json.JSONArray;
import java.util.Date;
import java.util.List;

public class Proportion {
    private Proportion(){

    }

    //We need to compute proportion using both cold start and increment (Need to get IV for historical data)

    public static float coldStart(List<Ticket> ticketList, Releases releases){
        float propSum = 0;
        for (Ticket ticket: ticketList){
            propSum = propSum + compute(ticket,releases);
        }
      return propSum/ (float) ticketList.size();
    }

    private static float compute(Ticket ticket, Releases releases) {
        int OV = 1;
        int FV = 1;
        int IV = 1;
        int count = 1;

        for(Release release:releases.getReleaseList()){
            if(ticket.injectedVersionDate().compareTo(release.getReleaseDate()) == 0){
                IV = count;
            }
            if(ticket.fixedVersionDate().compareTo(release.getReleaseDate()) == 0){
                FV = count;
            }
            if(ticket.openingVersionDate().compareTo(release.getReleaseDate()) == 0){
                OV = count;
            }
            count++;
        }

        return (float) (FV-IV+1)/ (float) (FV-OV+1); //Smoothing to consider the same version as distance one and therefore consider also tickets when IV = OV
    }

    public static Ticket createTicket(Date openingVersionDate, Date fixedVersionDate, JSONArray injectedVersion, List<Release> versions, String key) throws InvalidDataException {
        Date OV = openingVersionDate;
        String IV = injectedVersion.getJSONObject(0).getString("name");
        Date IVDate = null;
        Date FV = fixedVersionDate;
        boolean flagIV = false;

        for (int i = 1;i<versions.size()-1;i++) { //I don't consider first version as possible fixed version and last version as possible opening version (only resolved tickets)
            Date currentVersionDate = versions.get(i).getReleaseDate(); //Get the release date of a version
            if(openingVersionDate.before(currentVersionDate) && openingVersionDate.after(versions.get(i-1).getReleaseDate())){
                OV = (versions.get(i).getReleaseDate());
            }
            if(fixedVersionDate.after(currentVersionDate) && fixedVersionDate.before(versions.get(i+1).getReleaseDate())){
                FV = (versions.get(i+1).getReleaseDate());
            }
            if(!flagIV && IV.equals(versions.get(i).getName())){
                IVDate = versions.get(i).getReleaseDate();
                flagIV = true;
            }
        }

        if(IV.equals(versions.get(0).getName())){ //Check for injected version
            IVDate = versions.get(0).getReleaseDate();
        }else if(IV.equals(versions.get(versions.size()-1).getName())){
            IVDate = versions.get(versions.size()-1).getReleaseDate();
        }


        if(fixedVersionDate.before(versions.get(versions.size()-1).getReleaseDate()) && versions.size() > 1 && fixedVersionDate.after(versions.get(versions.size()-2).getReleaseDate())){
            FV = versions.get(versions.size()-1).getReleaseDate();
        }else if(FV.compareTo(fixedVersionDate) == 0){
            FV = versions.get(0).getReleaseDate();
        }
        if(fixedVersionDate.after(versions.get(versions.size()-1).getReleaseDate())){ //Do not consider if opening > last release date
            throw new InvalidDataException();
        }


        if(openingVersionDate.before(versions.get(versions.size()-1).getReleaseDate()) && versions.size() > 1 && openingVersionDate.after(versions.get(versions.size()-2).getReleaseDate())){
            OV = versions.get(versions.size()-1).getReleaseDate();
        }else if(OV.compareTo(openingVersionDate) == 0){
            OV = versions.get(0).getReleaseDate();
        }
        if(openingVersionDate.after(versions.get(versions.size()-1).getReleaseDate())){ //Do not consider if opening > last release date
            throw new InvalidDataException();
        }

        if(IVDate == null || OV.after(FV) || IVDate.after(OV) || IVDate.after(FV)){ //Don't consider FV==OV to apply smoothing
            throw new InvalidDataException();
        }

        return new Ticket(OV,FV,IVDate,IV,key);
    }

    public static Ticket createTicketWithoutProportionAdmissible(Date openingVersionDate, Date fixedVersionDate, int proportionValue, List<Release> versions, String key) throws InvalidDataException {
        Date OV = openingVersionDate;
        int OVIndex = 0;
        int FVIndex = 0;
        Date IVDate;
        Date FV = fixedVersionDate;


        for (int i = 1;i<versions.size()-1;i++) { //I don't consider first version as possible fixed version and last version as possible opening version (only resolved tickets)
            Date currentVersionDate = versions.get(i).getReleaseDate(); //Get the release date of a version
            if(openingVersionDate.before(currentVersionDate) && openingVersionDate.after(versions.get(i-1).getReleaseDate())){
                OV = (versions.get(i-1).getReleaseDate());
                OVIndex = i-1;
            }
            if(fixedVersionDate.after(currentVersionDate) && openingVersionDate.before(versions.get(i+1).getReleaseDate())){
                FV = (versions.get(i+1).getReleaseDate());
                FVIndex=i+1;
            }
        }


        if(fixedVersionDate.before(versions.get(versions.size()-1).getReleaseDate()) && versions.size() > 1 && fixedVersionDate.after(versions.get(versions.size()-2).getReleaseDate())){
            FV = versions.get(versions.size()-1).getReleaseDate();
            FVIndex = versions.size()-1;
        }else if(FV.compareTo(fixedVersionDate) == 0){
            FV = versions.get(0).getReleaseDate();
        }
        if(fixedVersionDate.after(versions.get(versions.size()-1).getReleaseDate())){ //Do not consider if opening > last release date
            throw new InvalidDataException();
        }


        if(openingVersionDate.before(versions.get(versions.size()-1).getReleaseDate()) && versions.size() > 1 && openingVersionDate.after(versions.get(versions.size()-2).getReleaseDate())){
            OV = versions.get(versions.size()-1).getReleaseDate();
            OVIndex = versions.size()-1;
        }else if(OV.compareTo(openingVersionDate) == 0){
            OV = versions.get(0).getReleaseDate();
        }
        if(openingVersionDate.after(versions.get(versions.size()-1).getReleaseDate())){ //Do not consider if opening > last release date
            throw new InvalidDataException();
        }



        //Get injected version using proportion value

        int IVIndex = FVIndex - (FVIndex - OVIndex) * proportionValue;

        if(IVIndex < 1){
            IVDate = versions.get(0).getReleaseDate();
            IVIndex = 0;
        }else if(IVIndex == FVIndex){
            IVIndex = FVIndex - 1;
            IVDate = versions.get(IVIndex).getReleaseDate();
        }else{
            IVDate = versions.get(IVIndex).getReleaseDate();
        }


        if(FV.compareTo(fixedVersionDate) == 0){
            FV = versions.get(0).getReleaseDate();
        }

        if(IVDate == null || OV.after(FV) || IVDate.after(OV) || IVDate.after(FV)){ //Don't consider FV==OV
            if(versions.get(IVIndex-1).getReleaseDate().before(FV)){ //When releases aren't ordered by date
                IVDate = versions.get(IVIndex-1).getReleaseDate();
                IVIndex = IVIndex -1;
            }else{
                throw new InvalidDataException();
            }
        }

        return new Ticket(OV,FV,IVDate,versions.get(IVIndex).getName(),key);
    }
}
