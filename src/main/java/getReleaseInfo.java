import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;


public class getReleaseInfo {
	public static HashMap<LocalDateTime, String> releaseNames;
	public static HashMap<LocalDateTime, String> releaseID;
	public static ArrayList<LocalDateTime> releases;
	public static Integer numVersions;

	public static void main(String[] args) throws IOException, JSONException {
		Initializer.getInstance();
		String projectName = Initializer.getProjectName();
		Object categories = Initializer.getCategoriesNames();
		System.out.println(categories);
		//Start from here


		//Fills the arraylist with releases dates and orders them
		//Ignores releases with missing dates
		releases = new ArrayList<>();
		int i;
		String url = "https://issues.apache.org/jira/rest/api/2/project/" + projectName;
		JSONObject resultSet = readJsonFromUrl(url);
		System.out.println(resultSet);
		JSONArray versions = resultSet.getJSONArray("versions");
		releaseNames = new HashMap<>();
		releaseID = new HashMap<>();
		for (i = 0; i < versions.length(); i++ ) {
			String name = "";
			String id = "";
			if(versions.getJSONObject(i).has("releaseDate")) {
				if (versions.getJSONObject(i).has("name"))
					name = versions.getJSONObject(i).get("name").toString();
				if (versions.getJSONObject(i).has("id"))
					id = versions.getJSONObject(i).get("id").toString();
				addRelease(versions.getJSONObject(i).get("releaseDate").toString(),
						name,id);
			}
		}

		releases.sort(LocalDateTime::compareTo);
		if (releases.size() < 6)
			return;
		FileWriter fileWriter = null;
		try {
			String outName = projectName + "VersionInfo.csv";
			//Name of CSV for output
			fileWriter = new FileWriter(outName);
			fileWriter.append("Index;Version ID;Version Name;Date");
			fileWriter.append("\n");
			numVersions = releases.size();
			for ( i = 0; i < releases.size(); i++) {
				int index = i + 1;
				fileWriter.append(Integer.toString(index));
				fileWriter.append(";");
				fileWriter.append(releaseID.get(releases.get(i)));
				fileWriter.append(";");
				fileWriter.append(releaseNames.get(releases.get(i)));
				fileWriter.append(";");
				fileWriter.append(releases.get(i).toString());
				fileWriter.append("\n");
			}

		} catch (Exception e) {
			System.out.println("Error in csv writer");
			e.printStackTrace();
		} finally {
			try {
				assert fileWriter != null;
				fileWriter.flush();
				fileWriter.close();
			} catch (IOException e) {
				System.out.println("Error while flushing/closing fileWriter !!!");
				e.printStackTrace();
			} catch(NullPointerException n){
				System.out.println("Null pointer exception while flushing");
			}
		}
	}


	public static void addRelease(String strDate, String name, String id) {
		LocalDate date = LocalDate.parse(strDate);
		LocalDateTime dateTime = date.atStartOfDay();
		if (!releases.contains(dateTime))
			releases.add(dateTime);
		releaseNames.put(dateTime, name);
		releaseID.put(dateTime, id);
	}


	@Contract("_ -> new")
	public static @NotNull JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
		try (InputStream is = new URL(url).openStream()) {
			BufferedReader rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
			String jsonText = readAll(rd);
			return new JSONObject(jsonText);
		}
	}

	@org.jetbrains.annotations.NotNull
	private static String readAll(@NotNull Reader rd) throws IOException {
		StringBuilder sb = new StringBuilder();
		int cp;
		while ((cp = rd.read()) != -1) {
			sb.append((char) cp);
		}
		return sb.toString();
	}


}