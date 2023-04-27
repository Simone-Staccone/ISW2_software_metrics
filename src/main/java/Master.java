import control.GitHubConnector;
import control.JiraConnector;
import exceptions.InvalidDataException;
import org.json.JSONException;
import utils.IO;
import utils.Initializer;

import java.util.List;

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
            GitHubConnector.getInfos();
            IO.appendOnLog("\n\t  FINISHED ANALYZING " + project);
            IO.appendOnLog("******************************************\n");
        }


        IO.appendOnLog("SOFTWARE METRICS ANALYZER SUCCESSFULLY STOPPED\n");
    }
}
