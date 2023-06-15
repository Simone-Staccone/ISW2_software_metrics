package control;

import exceptions.InvalidDataException;
import model.Releases;
import model.Ticket;
import org.json.JSONArray;
import org.json.JSONObject;
import utils.DateParser;
import utils.IO;
import utils.Initializer;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;



public class JiraConnector {
	private static List<List<String>> getVersionInfo(JSONArray versions, int num){
		List<List<String>> entries = new ArrayList<>();
		int len = Math.min(num, versions.length());
		for (int i = 0; i < len; i++ ) {
			List<String> entry = new ArrayList<>();
			if(versions.getJSONObject(i).has("releaseDate") && versions.getJSONObject(i).has("name") && versions.getJSONObject(i).has("id") && versions.getJSONObject(i).getBoolean("released")) { //Considering releases only with date, name and id
				entry.add(versions.getJSONObject(i).get("id").toString());
				entry.add(versions.getJSONObject(i).get("name").toString());
				entry.add(versions.getJSONObject(i).get("releaseDate").toString());
				entry.add(String.valueOf(i));
				entries.add(entry);
			}
		}
		return entries;
	}

	public float computeProportion(List<String> projects) {
		float prop = 0;
		IO.appendOnLog("Computing proportion with cold start ...");

		int i = 1;
		for (String project : projects) {
			float singleProportion;
			if (!Objects.equals(project, "BOOKKEEPER") && !Objects.equals(project, "OPENJPA")) {
				Releases versions = getInfos(project, "all");
				List<Ticket> ticketList;
				ticketList = JiraConnector.getTicketsWithAv(project,versions);
				singleProportion = Proportion.coldStart(ticketList, versions); //Compute proportion for each project
				prop = prop + singleProportion;

				IO.appendOnLog("Proportion computed for project " + project + " is: " + singleProportion + " number of tickets: " + ticketList.size());
				i++;
			}

		}

		IO.appendOnLog("Proportion value computed with cold start is: " + prop / 5);
		IO.appendOnLog("Proportion successfully acquired\n");

		return prop / i;
	}

	public Releases getInfos(String projectName, String number) {
		//Get the JSON result from the url to see all the issues
		Initializer initializer = Initializer.getInstance();
		JSONObject resultSet = IO.readJsonObject(initializer.getApiUrl() + projectName);
		assert resultSet != null;
		int num = resultSet.getJSONArray(ConstantNames.VERSIONS).length();
		if(number.compareTo("all") != 0){
			num = Integer.parseInt(number);
		}

		return new Releases(getVersionInfo(resultSet.getJSONArray(ConstantNames.VERSIONS) , num+1));
	}



	private static List<Ticket> getTicketsWithAv(String project, Releases versions){
		//Get the JSON result from the url to see all the issues
		Initializer initializer = Initializer.getInstance();
		JSONObject secondResultSet = IO.readJsonObject(initializer.getSearchUrlFirstHalf()
				+ project
				+ initializer.getSearchUrlSecondHalf());
		List<Ticket> ticketList = new ArrayList<>();

		for (int i = 0; i< Objects.requireNonNull(secondResultSet).getJSONArray(ConstantNames.ISSUES).length(); i++) {
			JSONObject fields = secondResultSet.getJSONArray(ConstantNames.ISSUES).getJSONObject(i).getJSONObject("fields");
			String key = secondResultSet.getJSONArray(ConstantNames.ISSUES).getJSONObject(i).getString("key");

			//Check only tickets with injected version
			if(fields.getJSONArray(ConstantNames.VERSIONS).length() != 0){
				Date openingVersionDate = DateParser.parseStringToDate(fields.getString("created").substring(0,ConstantNames.FORMATTING_STRING.length())); //While parsing, I intentionally lose information about hour and minutes of the ticket
				Date fixedVersionDate = DateParser.parseStringToDate(fields.getString("resolutiondate").substring(0,ConstantNames.FORMATTING_STRING.length()));
				JSONArray injectedVersions = fields.getJSONArray(ConstantNames.VERSIONS);


				assert openingVersionDate != null;
				assert fixedVersionDate != null;

				try {
					ticketList.add(Proportion.createTicket(openingVersionDate,fixedVersionDate,injectedVersions,versions.getReleaseList(),key));
				} catch (InvalidDataException ignored) {
					//We want to discard invalid tickets
				}
			}
		}


		return ticketList;
	}

	public List<Ticket> getTickets(String projectName, int proportionValue, Releases versions) {
		//Get the JSON result from the url to see all the issues
		Initializer initializer = Initializer.getInstance();
		JSONObject secondResultSet = IO.readJsonObject(initializer.getSearchUrlFirstHalf()
				+ projectName
				+ initializer.getSearchUrlSecondHalf());
		List<Ticket> ticketList = new ArrayList<>();


		for (int i = 0; i < Objects.requireNonNull(secondResultSet).getJSONArray(ConstantNames.ISSUES).length(); i++) {
			JSONObject fields = secondResultSet.getJSONArray(ConstantNames.ISSUES).getJSONObject(i).getJSONObject("fields");
			String key = secondResultSet.getJSONArray(ConstantNames.ISSUES).getJSONObject(i).getString("key");
			Date openingVersionDate = DateParser.parseStringToDate(fields.getString("created").substring(0, ConstantNames.FORMATTING_STRING.length())); //While parsing, I intentionally lose information about hour and minutes of the ticket
			Date fixedVersionDate = DateParser.parseStringToDate(fields.getString("resolutiondate").substring(0, ConstantNames.FORMATTING_STRING.length()));

			assert openingVersionDate != null;
			assert fixedVersionDate != null;

			try {
				//Check only tickets with injected version
				if (fields.getJSONArray(ConstantNames.VERSIONS).length() != 0) {

					JSONArray injectedVersions = fields.getJSONArray(ConstantNames.VERSIONS);
					ticketList.add(Proportion.createTicket(openingVersionDate, fixedVersionDate, injectedVersions, versions.getReleaseList(), key));
				} else {
					ticketList.add(Proportion.createTicketWithoutProportionAdmissible(openingVersionDate, fixedVersionDate, proportionValue, versions.getReleaseList(), key));
				}
			} catch (InvalidDataException ignored) {
				//Discard non valid tickets
			}


		}
		return ticketList;
	}
}