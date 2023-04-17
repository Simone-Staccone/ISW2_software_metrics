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
		JSONArray versions = resultSet.getJSONArray("versions");

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