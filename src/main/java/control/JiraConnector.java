package control;

import exceptions.InvalidDataException;
import model.Releases;
import model.apiresult.Issue;
import model.apiresult.TicketVersion;
import model.ticket.AffectedVersionTicket;
import model.ticket.ProportionTicket;
import model.ticket.Ticket;
import org.eclipse.jgit.revwalk.RevCommit;
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
	/**
	 * @param projectName The name of the project we are considering, taken from config file
	 * @throws InvalidDataException Exception thrown when the number of the tickets generated from the script doesn't match
	 * the total number of tickets given by the api
	 * @return TicketVersion is the set oh releases held by the project
	 */
	public TicketVersion getInfos(String projectName, List<RevCommit> commits) throws InvalidDataException {
		JSONObject resultSet = IO.readJsonObject(Initializer.getApiUrl() + projectName); //Get the JSON result from the url to see all the issues
		List<List<String>> entries = getVersionInfo(Objects.requireNonNull(resultSet).getJSONArray("versions"));
		TicketVersion apiResultVersion = new TicketVersion(entries,commits);



		JSONObject secondResultSet = IO.readJsonObject(Initializer.getSearchUrlFirstHalf()
														+ projectName
														+ Initializer.getSearchUrlSecondHalf()); //Get the JSON result from the url to see all the versions

		JSONArray issues = Objects.requireNonNull(secondResultSet).getJSONArray("issues");
		Issue apiResultIssues = new Issue(issues,apiResultVersion.getReleases());
		List<AffectedVersionTicket> affectedVersionTickets = apiResultIssues.getAffectedVersionTickets();
		List<ProportionTicket> proportionTickets  = apiResultIssues.getProportionTickets();
		int countAffected = affectedVersionTickets.size(), countProportion = proportionTickets.size();




		IO.appendOnLog("Issues with affected version: " + countAffected);
		IO.appendOnLog("Issues used in proportion: " + countProportion);
		IO.appendOnLog("Total issues: " + issues.length());
		IO.appendOnLog("Percentage of issues with affected versions: " + Math.round( ( (float) countAffected/ issues.length() ) * 10000.0) / 100.0 + "%");
		IO.appendOnLog("Percentage of issues used in proportion: " + Math.round( ( (float) countProportion/ issues.length() ) * 10000.0) / 100.0 + "%");
		IO.appendOnLog("Number of releases: " + apiResultVersion);

		if(IO.writeOnFile(projectName,entries)){
			IO.appendOnLog("File for " + projectName +  " project created correctly!");
		}else{
			IO.appendOnLog("ERROR: Error in writing on " + projectName + "project file!");
		}
		return apiResultVersion;
	}

	private static List<List<String>> getVersionInfo(JSONArray versions){
		List<List<String>> entries = new ArrayList<>();
		for (int i = 0; i < versions.length(); i++ ) {
			List<String> entry = new ArrayList<>();
			if(versions.getJSONObject(i).has("releaseDate") && versions.getJSONObject(i).has("name") && versions.getJSONObject(i).has("id")) { //Considering releases only with date, name and id
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
		IO.appendOnLog("Computing proportion with cold start ...");

		int i = 0;
		for (String project : projects) {
			List<Ticket> allAVTickets = new ArrayList<>();
			float singleProportion;
			if (!Objects.equals(project, "BOOKKEEPER") && !Objects.equals(project, "OPENJPA")) {
				Releases versions = getInfos(project);
				List<Ticket> ticketList = null;
				ticketList = JiraConnector.getTicketsWithAv(project,versions);
				if (ticketList != null) {
					allAVTickets = Stream.concat(allAVTickets.stream(), ticketList.stream())
							.distinct()
							.collect(Collectors.toList());
				}
				singleProportion = Proportion.coldStart(allAVTickets, versions); //Compute proportion for each project
				prop = prop + singleProportion;
				proportions.add(singleProportion);

				IO.appendOnLog("Proportion calculated for project " + project + " is: " + proportions.get(i) + " number of tickets: " + ticketList.size());
				i++;
			}

		}



		IO.appendOnLog("Proportion value computed with cold start is: " + prop / proportions.size());
		IO.appendOnLog("Proportion successfully acquired");
		return prop / proportions.size();
	}

	private Releases getInfos(String projectName) {
		JSONObject resultSet = IO.readJsonObject(Initializer.getApiUrl() + projectName); //Get the JSON result from the url to see all the issues
		return new Releases(getVersionInfo(Objects.requireNonNull(resultSet).getJSONArray("versions")));
	}

	private static List<Ticket> getTicketsWithAv(String project, Releases versions){
		JSONObject secondResultSet = IO.readJsonObject(Initializer.getSearchUrlFirstHalf()
				+ project
				+ Initializer.getSearchUrlSecondHalf()); //Get the JSON result from the url to see all the issues
		List<Ticket> ticketList = new ArrayList<>();

		for (int i = 0;i<secondResultSet.getJSONArray("issues").length();i++) {
			JSONObject fields = secondResultSet.getJSONArray("issues").getJSONObject(i).getJSONObject("fields");

			if(fields.getJSONArray("versions").length() != 0){ //Check only tickets with injected version
				Date openingVersionDate = DateParser.parseStringToDate(fields.getString("created").substring(0,ConstantNames.FORMATTING_STRING.length())); //While parsing I intentionally lose information about hour and minutes of the ticket
				Date fixedVersionDate = DateParser.parseStringToDate(fields.getString("resolutiondate").substring(0,ConstantNames.FORMATTING_STRING.length()));
				JSONArray injectedVersions = fields.getJSONArray("versions");

				if(openingVersionDate.before(versions.getLatestRelease().getReleaseDate()) //Add only if fixed version and opening version are both located before last release with a date
						&& fixedVersionDate.before(versions.getLatestRelease().getReleaseDate())
						&& openingVersionDate.before(fixedVersionDate)){ //Don't consider tickets with wrong dates
					try {
						ticketList.add(Proportion.createTicket(openingVersionDate,fixedVersionDate,injectedVersions,versions.getReleaseList()));
					} catch (InvalidDataException e) {
					}
				}
			}
		}

		System.out.println(project + " " + ticketList.size());


		return ticketList;
	}
}