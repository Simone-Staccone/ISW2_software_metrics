package control;

import exceptions.InvalidDataException;
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
    /**
     * @param issues result set obtained by jira API
     * @param affectedVersionTickets list pf tickets with affected version reference in the result set
     * @param proportionTickets list of tickets without affected version, to use to compute proportion
     * @throws InvalidDataException Exception raised when the total number of tickets doesn't match the sum of the split
     */
    public static void computeState(JSONArray issues, List<AffectedVersionTicket> affectedVersionTickets,  List<ProportionTicket> proportionTickets) throws InvalidDataException {
        for (int i = 0; i < issues.length(); i++) {
            JSONObject fields = issues.getJSONObject(i).getJSONObject("fields");
            Date openingVersion = getOpeningVersion(fields);
            List<String> components = getComponents(fields.getJSONArray("components"));
            AffectedVersion affectedVersion = getAffectedVersion(fields.getJSONArray("versions"));
            List<String> fixedVersions = new ArrayList<>();
            List<Date> fixedVersionsDate = new ArrayList<>();


            for (int j = 0; j < fields.getJSONArray("fixVersions").length(); j++) {
                fixedVersions.add(fields.getJSONArray("fixVersions").getJSONObject(j).toString());
                if(fields.getJSONArray("fixVersions").getJSONObject(j).has("releaseDate")){
                    try {
                        fixedVersionsDate.add(new SimpleDateFormat("dd/MM/yyyy").parse(fields.getJSONArray("fixVersions").getJSONObject(j).getString("releaseDate")));
                    } catch (ParseException e) {
                        fixedVersions.add(null);
                    }
                }
            }



            if (affectedVersion.id() == -1) {  //We consider only the tickets with affected version
                affectedVersionTickets.add(
                        new AffectedVersionTicket(
                                affectedVersion,
                                fixedVersions,
                                fixedVersionsDate,
                                components,
                                openingVersion
                        ));
            } else {
                proportionTickets.add(
                        new ProportionTicket(
                                fields.getString("created")
                        ));
            }
        }

        if (affectedVersionTickets.size() + proportionTickets.size() != issues.length()) {
            throw new InvalidDataException();
        }
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
        String formatString = "yyyy-MM-dd";

        try {
            System.out.println(fields.getString("created").substring(0,formatString.length()));
            openingVersion =  new SimpleDateFormat(formatString).parse(fields.getString("created").substring(0,formatString.length()));
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
            if(versions.getJSONObject(0).has("releaseDate")) {
                try {
                    releaseDate = new SimpleDateFormat("yyyy-MM-dd").parse(versions.getJSONObject(0).getString("releaseDate"));
                } catch (ParseException e) {
                    IO.appendOnLog("ERROR: release date wrongly formatted");
                    releaseDate = null;
                }
            }
        }
        return new AffectedVersion(id,name,releaseDate);
    }
}
