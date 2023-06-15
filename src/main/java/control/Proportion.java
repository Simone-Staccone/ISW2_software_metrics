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
      return propSum/ ticketList.size();
    }

    private static float compute(Ticket ticket, Releases releases) {
        int openingVersion = 1;
        int fixedVersion = 1;
        int injectedVersion = 1;
        int count = 1;

        for(Release release:releases.getReleaseList()){
            if(ticket.injectedVersionDate().compareTo(release.getReleaseDate()) == 0){
                injectedVersion = count;
            }
            if(ticket.fixedVersionDate().compareTo(release.getReleaseDate()) == 0){
                fixedVersion = count;
            }
            if(ticket.openingVersionDate().compareTo(release.getReleaseDate()) == 0){
                openingVersion = count;
            }
            count++;
        }

        return (float) (fixedVersion-injectedVersion+1)/ (float) (fixedVersion-openingVersion+1); //Smoothing to consider the same version as distance one and therefore consider also tickets when IV = OV
    }



    public static Ticket createTicket(Date openingVersionDate, Date fixedVersionDate, JSONArray injectedVersion, List<Release> versions, String key) throws InvalidDataException {
        Date actualOpeningVersion = openingVersionDate;
        String actualInjectedVersion = injectedVersion.getJSONObject(0).getString("name");
        Date actualInjectedVersionDate = null;
        Date actualFixedVersion = fixedVersionDate;
        boolean flagIV = false;

        for (int i = 1;i<versions.size()-1;i++) { //I don't consider first version as possible fixed version and last version as possible opening version (only resolved tickets)
            Date currentVersionDate = versions.get(i).getReleaseDate(); //Get the release date of a version
            if(openingVersionDate.before(currentVersionDate) && openingVersionDate.after(versions.get(i-1).getReleaseDate())){
                actualOpeningVersion = (versions.get(i).getReleaseDate());
            }
            if(fixedVersionDate.after(currentVersionDate) && fixedVersionDate.before(versions.get(i+1).getReleaseDate())){
                actualFixedVersion = (versions.get(i+1).getReleaseDate());
            }
            if(!flagIV && actualInjectedVersion.equals(versions.get(i).getName())){
                actualInjectedVersionDate = versions.get(i).getReleaseDate();
                flagIV = true;
            }
        }


        actualInjectedVersionDate = getActualInjectedVersion(actualInjectedVersion,versions,actualInjectedVersionDate);

        actualFixedVersion = getActualFixedVersion(fixedVersionDate,versions,actualFixedVersion);

        actualOpeningVersion = getActualOpeningVersion(openingVersionDate,versions,actualOpeningVersion);

        consistencyCheck(fixedVersionDate,openingVersionDate,versions,actualInjectedVersionDate,actualOpeningVersion,actualFixedVersion);



        return new Ticket(actualOpeningVersion,actualFixedVersion,actualInjectedVersionDate,actualInjectedVersion,key);
    }

    private static Date getActualInjectedVersion(String actualInjectedVersion, List<Release> versions, Date actualInjectedVersionDate) {
        if(actualInjectedVersion.equals(versions.get(0).getName())){ //Check for injected version
            return versions.get(0).getReleaseDate();
        }else if(actualInjectedVersion.equals(versions.get(versions.size()-1).getName())){
            return  versions.get(versions.size()-1).getReleaseDate();
        }
        return actualInjectedVersionDate;
    }


    private static Date getActualFixedVersion(Date fixedVersionDate, List<Release> versions, Date actualFixedVersion) {
        if(fixedVersionDate.before(versions.get(versions.size()-1).getReleaseDate()) && versions.size() > 1 && fixedVersionDate.after(versions.get(versions.size()-2).getReleaseDate())){
            return  versions.get(versions.size()-1).getReleaseDate();
        }else if(actualFixedVersion.compareTo(fixedVersionDate) == 0){
            return  versions.get(0).getReleaseDate();
        }
        return actualFixedVersion;
    }

    private static Date getActualOpeningVersion(Date openingVersionDate, List<Release> versions, Date actualOpeningVersion) {
        if(openingVersionDate.before(versions.get(versions.size()-1).getReleaseDate()) && versions.size() > 1 && openingVersionDate.after(versions.get(versions.size()-2).getReleaseDate())){
            return versions.get(versions.size()-1).getReleaseDate();
        }else if(actualOpeningVersion.compareTo(openingVersionDate) == 0){
            return versions.get(0).getReleaseDate();
        }
        return actualOpeningVersion;
    }


    private static void consistencyCheck(Date fixedVersionDate, Date openingVersionDate, List<Release> versions, Date actualInjectedVersionDate, Date actualOpeningVersion, Date actualFixedVersion) throws InvalidDataException {
        if(fixedVersionDate.after(versions.get(versions.size()-1).getReleaseDate())){ //Do not consider if opening > last release date
            throw new InvalidDataException();
        }


        if(openingVersionDate.after(versions.get(versions.size()-1).getReleaseDate())){ //Do not consider if opening > last release date
            throw new InvalidDataException();
        }

        if(actualInjectedVersionDate == null || actualOpeningVersion.after(actualFixedVersion) || actualInjectedVersionDate.after(actualOpeningVersion) || actualInjectedVersionDate.after(actualFixedVersion)){ //Don't consider FV==OV to apply smoothing
            throw new InvalidDataException();
        }
    }




    public static Ticket createTicketWithoutProportionAdmissible(Date openingVersionDate, Date fixedVersionDate, int proportionValue, List<Release> versions, String key) throws InvalidDataException {
        Date actualOpeningVersion = openingVersionDate;
        int actualOpeningVersionIndex = 0;
        int actualFixedVersionIndex = 0;
        Date actualInjectedVersionDate;
        Date actualFixedVersion = fixedVersionDate;

        for (int i = 1;i<versions.size()-1;i++) { //I don't consider first version as possible fixed version and last version as possible opening version (only resolved tickets)
            Date currentVersionDate = versions.get(i).getReleaseDate(); //Get the release date of a version
            if(openingVersionDate.before(currentVersionDate) && openingVersionDate.after(versions.get(i-1).getReleaseDate())){
                actualOpeningVersion = (versions.get(i-1).getReleaseDate());
                actualOpeningVersionIndex = i-1;
            }
            if(fixedVersionDate.after(currentVersionDate) && openingVersionDate.before(versions.get(i+1).getReleaseDate())){
                actualFixedVersion = (versions.get(i+1).getReleaseDate());
                actualFixedVersionIndex=i+1;
            }
        }


        if(fixedVersionDate.before(versions.get(versions.size()-1).getReleaseDate()) && versions.size() > 1 && fixedVersionDate.after(versions.get(versions.size()-2).getReleaseDate())){
            actualFixedVersion = versions.get(versions.size()-1).getReleaseDate();
            actualFixedVersionIndex = versions.size()-1;
        }else if(actualFixedVersion.compareTo(fixedVersionDate) == 0){
            actualFixedVersion = versions.get(0).getReleaseDate();
        }




        if(openingVersionDate.before(versions.get(versions.size()-1).getReleaseDate()) && versions.size() > 1 && openingVersionDate.after(versions.get(versions.size()-2).getReleaseDate())){
            actualOpeningVersion = versions.get(versions.size()-1).getReleaseDate();
            actualOpeningVersionIndex = versions.size()-1;
        }else if(actualOpeningVersion.compareTo(openingVersionDate) == 0){
            actualOpeningVersion = versions.get(0).getReleaseDate();
        }


        int actualInjectedVersionIndex = actualFixedVersionIndex - (actualFixedVersionIndex - actualOpeningVersionIndex) * proportionValue;

        //Get injected version using proportion value
        actualInjectedVersionDate = versions.get(actualInjectedVersionIndex).getReleaseDate();

        if(actualInjectedVersionIndex < 1){
            actualInjectedVersionDate = versions.get(0).getReleaseDate();
            actualInjectedVersionIndex = 0;
        }else if(actualInjectedVersionIndex == actualFixedVersionIndex){
            actualInjectedVersionIndex = actualFixedVersionIndex - 1;
            actualInjectedVersionDate = versions.get(actualInjectedVersionIndex).getReleaseDate();
        }




        actualFixedVersion = checkActualFIxedVersion(actualFixedVersion,fixedVersionDate,versions);



        actualInjectedVersionDate = checkConsistencyWithoutProportion(fixedVersionDate,openingVersionDate,actualInjectedVersionDate,actualOpeningVersion,actualFixedVersion,versions,actualInjectedVersionIndex );

        actualInjectedVersionIndex = actualInjectedVersionIndex -1;

        return new Ticket(actualOpeningVersion,actualFixedVersion,actualInjectedVersionDate,versions.get(actualInjectedVersionIndex).getName(),key);
    }

    private static Date checkActualFIxedVersion(Date actualFixedVersion, Date fixedVersionDate, List<Release> versions) {
        if(actualFixedVersion.compareTo(fixedVersionDate) == 0){
            return versions.get(0).getReleaseDate();
        }
        return actualFixedVersion;
    }


    private static Date checkConsistencyWithoutProportion(Date fixedVersionDate, Date openingVersionDate, Date actualInjectedVersionDate, Date actualOpeningVersion, Date actualFixedVersion, List<Release> versions, int actualInjectedVersionIndex) throws InvalidDataException {
        if(openingVersionDate.after(versions.get(versions.size()-1).getReleaseDate())){ //Do not consider if opening > last release date
            throw new InvalidDataException();
        }
        if(fixedVersionDate.after(versions.get(versions.size()-1).getReleaseDate())){ //Do not consider if opening > last release date
            throw new InvalidDataException();
        }

        if(actualInjectedVersionDate == null || actualOpeningVersion.after(actualFixedVersion) || actualInjectedVersionDate.after(actualOpeningVersion) || actualInjectedVersionDate.after(actualFixedVersion)){ //Don't consider FV==OV
            if(versions.get(actualInjectedVersionIndex-1).getReleaseDate().before(actualFixedVersion)){ //When releases aren't ordered by date
                return versions.get(actualInjectedVersionIndex-1).getReleaseDate();
            }else{
                throw new InvalidDataException();
            }
        }
        return null;
    }

}
