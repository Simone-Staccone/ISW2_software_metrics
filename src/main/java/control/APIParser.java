package control;

import exceptions.InvalidDataException;
import model.Release;
import model.ticket.AffectedVersionTicket;
import model.ticket.ProportionTicket;
import model.verions.AffectedVersion;
import org.json.JSONArray;
import org.json.JSONObject;
import utils.IO;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class APIParser {
    private APIParser(){}
    /**
     * @param issues result set obtained by jira API
     * @param affectedVersionTickets list pf tickets with affected version reference in the result set
     * @param proportionTickets list of tickets without affected version, to use to compute proportion
     * @param releases
     * @throws InvalidDataException Exception raised when the total number of tickets doesn't match the sum of the split
     */
    public static void computeState(JSONArray issues, List<AffectedVersionTicket> affectedVersionTickets, List<ProportionTicket> proportionTickets, List<Release> releases) throws InvalidDataException, ParseException {
        for (int i = 0; i < issues.length(); i++) {
            JSONObject fields = issues.getJSONObject(i).getJSONObject("fields");
            Date openingVersion = getOpeningVersion(fields);
            List<String> components = getComponents(fields.getJSONArray("components"));
            AffectedVersion affectedVersion = getAffectedVersion(fields.getJSONArray("versions"));
            Date fixedVersion = getFixedVersion(fields.getString("resolutiondate"),releases);



            if (affectedVersion.id() == -1) {  //We consider only the tickets with affected version
                affectedVersionTickets.add(
                        new AffectedVersionTicket(
                                affectedVersion,
                                components,
                                openingVersion,
                                fixedVersion
                        ));
            } else {
                proportionTickets.add(
                        new ProportionTicket(
                                components,
                                openingVersion
                        ));
            }
        }

        if (affectedVersionTickets.size() + proportionTickets.size() != issues.length()) {
            throw new InvalidDataException();
        }
    }

    private static Date getFixedVersion(String fixDate, List<Release> dates) throws ParseException {
        //Compute Fixed version as the following release of the date of fix
        Date fixedVersion = new Date();

        for (Release release:dates) {
            if(new SimpleDateFormat(ConstantNames.FORMATTING_STRING).parse(fixDate).after(release.getReleaseDate())){
                fixedVersion = new SimpleDateFormat(ConstantNames.FORMATTING_STRING).parse(fixDate);
                break;
            }
        }
        return fixedVersion;
    }

    private static List<String> getComponents(JSONArray components) {
        List<String> localComponents = new ArrayList<>();
        for (int j = 0; j < components.length(); j++) {
            localComponents.add(components.getJSONObject(j).toString());
        }
        return localComponents;
    }

    private static Date getOpeningVersion(JSONObject fields) {
        Date openingVersion = null;

        try {
            openingVersion =  new SimpleDateFormat(ConstantNames.FORMATTING_STRING).parse(fields.getString(ConstantNames.CREATED).substring(0,ConstantNames.FORMATTING_STRING.length()));
        } catch (ParseException e) {
            IO.appendOnLog("ERROR: No opening version found");
        }
        return openingVersion;
    }

    private static AffectedVersion getAffectedVersion(JSONArray versions) {
        int id = -1;
        String name = "";
        Date releaseDate = new Date();

        if(versions.length() != 0) {
            id = Integer.parseInt(versions.getJSONObject(0).getString("id"));
            name = versions.getJSONObject(0).getString("name");
            if(versions.getJSONObject(0).has(ConstantNames.FORMATTING_STRING)) {
                try {
                    releaseDate = new SimpleDateFormat(ConstantNames.FORMATTING_STRING).parse(versions.getJSONObject(0).getString(ConstantNames.RELEASE_DATE));
                } catch (ParseException e) {
                    IO.appendOnLog("ERROR: release date wrongly formatted");
                    releaseDate = null;
                }
            }
        }
        return new AffectedVersion(id,name,releaseDate);
    }
}
