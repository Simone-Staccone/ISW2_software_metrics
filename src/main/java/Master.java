import control.GitHubConnector;
import control.JiraConnector;
import exceptions.InvalidDataException;
import org.json.JSONException;
import utils.Initializer;

import java.util.List;

public class Master {
    public static void main(String[] args) throws JSONException {
        Initializer.getInstance();
        JiraConnector jiraConnector = new JiraConnector();
        List<String> projects = Initializer.getProjectNames();
        for (String project : projects) {
            System.out.println("******************************************");
            System.out.println("\t  START ANALYZING " + project );
            System.out.println();
            try {
                jiraConnector.getInfos(project);
            } catch (InvalidDataException e) {
                System.err.println("Error in data split");
            }
            GitHubConnector.getInfos();
            System.out.println();
            System.out.println("\t FINISHED ANALYZING " + project);
            System.out.println("******************************************");
            System.out.println();
        }
    }
}
