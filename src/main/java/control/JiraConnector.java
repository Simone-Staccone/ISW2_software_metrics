package control;

import exceptions.InvalidDataException;
import model.AffectedVersionTicket;
import model.ProportionTicket;
import org.json.JSONArray;
import org.json.JSONObject;
import utils.IO;
import utils.Initializer;


import java.util.ArrayList;
import java.util.List;


public class JiraConnector {
	public void getInfos(String projectName) throws InvalidDataException {
		JSONObject resultSet = IO.readJsonFromUrl(Initializer.getApiUrl() + projectName);
		JSONObject secondResultSet = IO.readJsonFromUrl(Initializer.getSearchUrlFirstHalf() + projectName + Initializer.getSearchUrlSecondHalf());
		JSONArray issues = secondResultSet.getJSONArray("issues");
		List<AffectedVersionTicket> affectedVersionTickets = new ArrayList<>();
		List<ProportionTicket> proportionTickets  = new ArrayList<>();
		int countAffected = 0, countProportion = 0;
		List<List<String>> entries = getVersionInfo(resultSet.getJSONArray("versions"));


		for(int i = 0; i < issues.length();i++){
			JSONObject issue = issues.getJSONObject(i);
			JSONObject fields = issue.getJSONObject("fields");
			if(fields.getJSONArray("versions").length() != 0){  //We consider only the tickets with affected version
				countAffected++;
				affectedVersionTickets.add(
						new AffectedVersionTicket(
								fields.getJSONArray("versions").toString(),
								fields.getJSONArray("fixVersions").toString(),
								"",
								"fields.getJSONArray('versions').getJSONObject(0).getString('releaseDate')", //Check well if object existed in versions
								"",
								"entries.get(0).get(2)"
						));
			}else{
				countProportion++;
				proportionTickets.add(
						new ProportionTicket(
								fields.getString("created")
						));
			}

		}

		if(countAffected+countProportion != issues.length()){
			throw new InvalidDataException();
		}

		System.out.println("Issues with affected version: " + countAffected);
		System.out.println("Issues used in proportion: " + countProportion);
		System.out.println("Total issues: " + issues.length());
		System.out.println("Percentage of issues with affected versions: " + Math.round( ( (float) countAffected/ issues.length() ) * 10000.0) / 100.0 + "%");
		System.out.println("Percentage of issues used in proportion: " + Math.round( ( (float) countProportion/ issues.length() ) * 10000.0) / 100.0 + "%");



		if(IO.writeOnFile(projectName,entries)){
			System.out.println("File for " + projectName +  " project created correctly!");
		}else{
			System.out.println("Error in writing on " + projectName + "project file!");
		}
	}

	private static List<List<String>> getVersionInfo(JSONArray versions){
		List<List<String>> entries = new ArrayList<>();
		for (int i = 0; i < versions.length(); i++ ) {
			List<String> entry = new ArrayList();
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