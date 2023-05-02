import control.GitHubConnector;
import control.JiraConnector;
import exceptions.InvalidDataException;
import model.Release;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.revwalk.RevCommit;
import org.json.JSONException;
import utils.IO;
import utils.Initializer;


import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class Master {
    public static void main(String[] args) throws JSONException {

        Initializer.getInstance();
        IO.appendOnLog("STARTING SOFTWARE METRICS ANALYZER\n");

        JiraConnector jiraConnector = new JiraConnector();
        List<String> projects = Initializer.getProjectNames(); //Start up the analyzer
        jiraConnector.computeProportion(projects); //Get proportion as cold start for the projects which aren't bookkeeper and openjpa


/*
        for (String project : projects) {
            IO.appendOnLog("******************************************");
            IO.appendOnLog("\t  START ANALYZING " + project + "\n");
            try {
                //Insert proportion calculation

                if (Objects.equals(project, "BOOKKEEPER") || Objects.equals(project, "OPENJPA")) {
                    List<RevCommit> commits = GitHubConnector.getCommits(project);
                    List<Release> releases = jiraConnector.getInfos(project,commits).releases;
                    IO.appendOnLog("Computing data set for project: " + project.toLowerCase());
                    GitHubConnector.buildDataSet(project, commits);
                    IO.appendOnLog("Data set successfully saved for project: " + project.toLowerCase());
                }


            } catch (InvalidDataException | IOException | GitAPIException e) {
                IO.appendOnLog("ERROR: Error in data split");
            }



            IO.appendOnLog("\n\t  FINISHED ANALYZING " + project);
            IO.appendOnLog("******************************************\n");
        }
*/

        IO.appendOnLog("SOFTWARE METRICS ANALYZER SUCCESSFULLY STOPPED\n");
    }
}
