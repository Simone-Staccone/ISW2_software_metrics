import control.GitHubConnector;
import control.JiraConnector;
import exceptions.InvalidDataException;
import org.eclipse.jgit.api.errors.GitAPIException;
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
        List<String> projects = Initializer.getProjectNames();

        for (String project : projects) {
            IO.appendOnLog("******************************************");
            IO.appendOnLog("\t  START ANALYZING " + project + "\n");
            try {
                jiraConnector.getInfos(project);
            } catch (InvalidDataException e) {
                IO.appendOnLog("ERROR: Error in data split");
            }

            //Insert proportion calculation

            if( Objects.equals(project, "BOOKKEEPER") || Objects.equals(project, "OPENJPA") ) {
                try {
                    IO.appendOnLog("Computing data set for project: " + project.toLowerCase());
                    GitHubConnector.buildDataSet(project);
                    IO.appendOnLog("Data set successfully saved for project: " + project.toLowerCase());
                } catch (IOException | GitAPIException e) {
                    e.printStackTrace();
                }
            }

            IO.appendOnLog("\n\t  FINISHED ANALYZING " + project);
            IO.appendOnLog("******************************************\n");
        }


        IO.appendOnLog("SOFTWARE METRICS ANALYZER SUCCESSFULLY STOPPED\n");
    }
}
