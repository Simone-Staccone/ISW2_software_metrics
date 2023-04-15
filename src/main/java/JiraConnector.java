import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;


public class JiraConnector {
	private static final String CSV_SEPARATOR = ";";

	public static void getInfos(String projectName) {
		List<List<String>> entries = new ArrayList<>();
		JSONObject resultSet = readJsonFromUrl(Initializer.getApiUrl() + projectName);
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

		try{
			FileWriter fileWriter;
			fileWriter = new FileWriter(projectName + Initializer.getOutputFileNameTail());
			fileWriter.append("Index;Version ID;Version Name;Date\n");
			int index = 0;

			for (List<String> ledge : entries) {
				fileWriter.append(String.valueOf(index)).append(CSV_SEPARATOR).append(ledge.get(0)).append(CSV_SEPARATOR).append(ledge.get(1)).append(CSV_SEPARATOR)
						.append(ledge.get(2)).append("\n");
				index=index+1;
			}
			fileWriter.flush();
			fileWriter.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	private static JSONObject readJsonFromUrl(String url)  {
		try (InputStream is = new URL(url).openStream()) {
			BufferedReader rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
			String jsonText = readAll(rd);
			return new JSONObject(jsonText);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static String readAll(Reader rd) throws IOException {
		StringBuilder sb = new StringBuilder();
		int cp;
		while ((cp = rd.read()) != -1) {
			sb.append((char) cp);
		}
		return sb.toString();
	}


}