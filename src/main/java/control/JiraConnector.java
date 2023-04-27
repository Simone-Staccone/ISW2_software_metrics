package control;

import exceptions.InvalidDataException;
import model.apiresult.Issue;
import model.ticket.AffectedVersionTicket;
import model.ticket.ProportionTicket;
import org.json.JSONArray;
import org.json.JSONObject;
import utils.IO;
import utils.Initializer;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class JiraConnector {
	/**
	 * @param projectName The name of the project we are considering, taken from config file
	 * @throws InvalidDataException Exception thrown when the number of the tickets generated from the script doesn't match
	 * the total number of tickets given by the api
	 */
	public void getInfos(String projectName) throws InvalidDataException {
		JSONObject resultSet = IO.readJsonFromUrl(Initializer.getApiUrl() + projectName); //Get the JSON result from the url to see all the issues
		JSONObject secondResultSet = IO.readJsonFromUrl(Initializer.getSearchUrlFirstHalf()
														+ projectName
														+ Initializer.getSearchUrlSecondHalf()); //Get the JSON result from the url to see all the versions

		JSONArray issues = Objects.requireNonNull(secondResultSet).getJSONArray("issues");
		Issue apiResult = new Issue(issues);
		List<AffectedVersionTicket> affectedVersionTickets = apiResult.getAffectedVersionTickets();
		List<ProportionTicket> proportionTickets  = apiResult.getProportionTickets();
		int countAffected = affectedVersionTickets.size(), countProportion = proportionTickets.size();

		List<List<String>> entries = getVersionInfo(Objects.requireNonNull(resultSet).getJSONArray("versions"));


		IO.appendOnLog("Issues with affected version: " + countAffected);
		IO.appendOnLog("Issues used in proportion: " + countProportion);
		IO.appendOnLog("Total issues: " + issues.length());
		IO.appendOnLog("Percentage of issues with affected versions: " + Math.round( ( (float) countAffected/ issues.length() ) * 10000.0) / 100.0 + "%");
		IO.appendOnLog("Percentage of issues used in proportion: " + Math.round( ( (float) countProportion/ issues.length() ) * 10000.0) / 100.0 + "%");


		if(IO.writeOnFile(projectName,entries)){
			IO.appendOnLog("File for " + projectName +  " project created correctly!");
		}else{
			IO.appendOnLog("ERROR: Error in writing on " + projectName + "project file!");
		}
	}

	private static List<List<String>> getVersionInfo(JSONArray versions){
		List<List<String>> entries = new ArrayList<>();
		for (int i = 0; i < versions.length(); i++ ) {
			List<String> entry = new ArrayList<>();
			if(versions.getJSONObject(i).has("releaseDate") && versions.getJSONObject(i).has("name") && versions.getJSONObject(i).has("id")) {
				entry.add(versions.getJSONObject(i).get("id").toString());
				entry.add(versions.getJSONObject(i).get("name").toString());
				entry.add(versions.getJSONObject(i).get("releaseDate").toString());
				entries.add(entry);
			}
		}
		return entries;
	}

}