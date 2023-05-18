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
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class JiraConnector {
	private static List<List<String>> getVersionInfo(JSONArray versions, int num){
		List<List<String>> entries = new ArrayList<>();
		int len = Math.min(num, versions.length());//num<versions.length() ? num : versions.length();
		for (int i = 0; i < len; i++ ) { //I take only half of the versions
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
		List<Float> proportions = new ArrayList<>();
		float prop = 0;
		int totalSize = 0;
		IO.appendOnLog("Computing proportion with cold start ...");

		int i = 0;
		for (String project : projects) {
			List<Ticket> allAVTickets = new ArrayList<>();
			float singleProportion;
			if (!Objects.equals(project, "BOOKKEEPER") && !Objects.equals(project, "OPENJPA")) {
				Releases versions = getInfos(project, "all");
				List<Ticket> ticketList;
				ticketList = JiraConnector.getTicketsWithAv(project,versions);
				allAVTickets = Stream.concat(allAVTickets.stream(), ticketList.stream())
						.distinct()
						.collect(Collectors.toList());
				singleProportion = Proportion.coldStart(allAVTickets, versions); //Compute proportion for each project
				prop = prop + singleProportion;//*allAVTickets.size();  //Weighted avarege
				proportions.add(singleProportion);

				totalSize = totalSize + allAVTickets.size();
				IO.appendOnLog("Proportion computed for project " + project + " is: " + proportions.get(i) + " number of tickets: " + ticketList.size());
				i++;
			}

		}



		IO.appendOnLog("Proportion value computed with cold start is: " + prop / 5);
		IO.appendOnLog("Proportion successfully acquired\n");


		float finalProportion = 0;


		for (float proportion :proportions){
			finalProportion  = finalProportion + proportion;
		}

		return prop / 5;
	}

	public Releases getInfos(String projectName, String number) {
		JSONObject resultSet = IO.readJsonObject(Initializer.getApiUrl() + projectName); //Get the JSON result from the url to see all the issues
		assert resultSet != null;
		int num = resultSet.length();
		if(number.compareTo("all") != 0){
			num = Integer.parseInt(number);
		}

		return new Releases(getVersionInfo(resultSet.getJSONArray("versions") , num));
	}



	private static List<Ticket> getTicketsWithAv(String project, Releases versions){
		JSONObject secondResultSet = IO.readJsonObject(Initializer.getSearchUrlFirstHalf()
				+ project
				+ Initializer.getSearchUrlSecondHalf()); //Get the JSON result from the url to see all the issues
		List<Ticket> ticketList = new ArrayList<>();

		for (int i = 0; i< Objects.requireNonNull(secondResultSet).getJSONArray("issues").length(); i++) {
			JSONObject fields = secondResultSet.getJSONArray("issues").getJSONObject(i).getJSONObject("fields");
			String key = secondResultSet.getJSONArray("issues").getJSONObject(i).getString("key");

			if(fields.getJSONArray("versions").length() != 0){ //Check only tickets with injected version
				Date openingVersionDate = DateParser.parseStringToDate(fields.getString("created").substring(0,ConstantNames.FORMATTING_STRING.length())); //While parsing, I intentionally lose information about hour and minutes of the ticket
				Date fixedVersionDate = DateParser.parseStringToDate(fields.getString("resolutiondate").substring(0,ConstantNames.FORMATTING_STRING.length()));
				JSONArray injectedVersions = fields.getJSONArray("versions");


				assert openingVersionDate != null;
				assert fixedVersionDate != null;

				if(openingVersionDate.before(versions.getLatestRelease().getReleaseDate()) //Add only if fixed version and opening version are both located before last release with a date
						&& fixedVersionDate.before(versions.getLatestRelease().getReleaseDate())
						&& openingVersionDate.before(fixedVersionDate)){ //Don't consider tickets with wrong dates
					try {
						ticketList.add(Proportion.createTicket(openingVersionDate,fixedVersionDate,injectedVersions,versions.getReleaseList(),key));
					} catch (InvalidDataException ignored) {
					}
				}
			}
		}

		return ticketList;
	}

	public List<Ticket> getTickets(String projectName, int proportionValue, Releases versions) {
		JSONObject secondResultSet = IO.readJsonObject(Initializer.getSearchUrlFirstHalf()
				+ projectName
				+ Initializer.getSearchUrlSecondHalf()); //Get the JSON result from the url to see all the issues
		List<Ticket> ticketList = new ArrayList<>();

		for (int i = 0; i< Objects.requireNonNull(secondResultSet).getJSONArray("issues").length(); i++) {
			JSONObject fields = secondResultSet.getJSONArray("issues").getJSONObject(i).getJSONObject("fields");
			String key = secondResultSet.getJSONArray("issues").getJSONObject(i).getString("key");
			Date openingVersionDate = DateParser.parseStringToDate(fields.getString("created").substring(0,ConstantNames.FORMATTING_STRING.length())); //While parsing, I intentionally lose information about hour and minutes of the ticket
			Date fixedVersionDate = DateParser.parseStringToDate(fields.getString("resolutiondate").substring(0,ConstantNames.FORMATTING_STRING.length()));

			assert openingVersionDate != null;
			assert fixedVersionDate != null;

			if (openingVersionDate.before(versions.getLatestRelease().getReleaseDate()) //Add only if fixed version and opening version are both located before last release with a date
					&& fixedVersionDate.before(versions.getLatestRelease().getReleaseDate())
					&& openingVersionDate.before(fixedVersionDate)) { //Don't consider tickets with wrong dates
				try {
					if (fields.getJSONArray("versions").length() != 0) { //Check only tickets with injected version
						JSONArray injectedVersions = fields.getJSONArray("versions");
						ticketList.add(Proportion.createTicket(openingVersionDate, fixedVersionDate, injectedVersions, versions.getReleaseList(), key));
					} else {
						ticketList.add(Proportion.createTicketWithoutProportionAdmissible(openingVersionDate, fixedVersionDate, proportionValue, versions.getReleaseList(), key));
					}
				} catch (InvalidDataException ignored) {
				}
			}
		}
		return ticketList;
	}
}