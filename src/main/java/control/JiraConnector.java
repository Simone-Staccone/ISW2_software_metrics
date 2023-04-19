package control;

import model.Ticket;
import org.json.JSONArray;
import org.json.JSONObject;
import utils.IO;
import utils.Initializer;


import java.util.ArrayList;
import java.util.List;


public class JiraConnector {
	public static void getInfos(String projectName) {
		List<List<String>> entries = new ArrayList<>();
		JSONObject resultSet = IO.readJsonFromUrl(Initializer.getApiUrl() + projectName);
		JSONObject secondResultSet = IO.readJsonFromUrl(Initializer.getSearchUrlFirstHalf() + projectName + Initializer.getSearchUrlSecondHalf());
		JSONArray versions = resultSet.getJSONArray("versions");
		JSONArray issues = secondResultSet.getJSONArray("issues");
		List<Ticket> ticketList = new ArrayList<>();
		int count = 0;

		for(int i = 0; i < issues.length();i++){
			JSONObject issue = issues.getJSONObject(i);
			JSONObject fields = issue.getJSONObject("fields");
			if(fields.getJSONArray("versions").length() != 0){  //We consider only the tickets with affected version
				count++;
			}
			ticketList.add(
					new Ticket(
							fields.getJSONArray("versions").toString(),
							fields.getJSONArray("fixVersions").toString(),
							"",
							"fields.getJSONArray('versions').getJSONObject(0).getString('releaseDate')", //Check well if object existed in versions
							""
					));

		}
		System.out.println("Issues with affected version: " + count);
		System.out.println("Total issues: " + issues.length());
		System.out.println("Percentage of issues with affected versions: " + Math.round( ( (float) count/ issues.length() ) * 10000.0) / 100.0 + "%");


		for (int i = 0; i < versions.length(); i++ ) {
			List<String> entry = new ArrayList();
			if(versions.getJSONObject(i).has("releaseDate") && versions.getJSONObject(i).has("name") && versions.getJSONObject(i).has("id")) {
				entry.add(versions.getJSONObject(i).get("id").toString());
				entry.add(versions.getJSONObject(i).get("name").toString());
				entry.add(versions.getJSONObject(i).get("releaseDate").toString());
				entries.add(entry);
			}
		}
		if(IO.writeOnFile(projectName,entries)){
			System.out.println("File for " + projectName +  " project created correctly!");
		}else{
			System.out.println("Error in writing on " + projectName + "project file!");
		}
	}
}