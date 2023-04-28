import control.GitHubConnector;
import control.JiraConnector;
import exceptions.InvalidDataException;
import model.Commit;
import org.json.JSONException;
import utils.IO;
import utils.Initializer;

import java.util.List;
import java.util.Objects;

public class Master {
    public static void main(String[] args) throws JSONException {
        Initializer.getInstance();
        IO.appendOnLog("STARTING SOFTWARE METRICS ANALYZER\n");

        JiraConnector jiraConnector = new JiraConnector();
        List<String> projects = Initializer.getProjectNames();
        List<Commit> commits;
        List<String> classes;


        for (String project : projects) {
            IO.appendOnLog("******************************************");
            IO.appendOnLog("\t  START ANALYZING " + project + "\n");
            try {
                jiraConnector.getInfos(project);
            } catch (InvalidDataException e) {
                IO.appendOnLog("ERROR: Error in data split");
            }

            //Insert proportion calculation

            if( !Objects.equals(project, "BOOKKEEPER") && !Objects.equals(project, "OPENJPA") ) {
                //TODO
                //Evaluate proportion of project different from BOOKKEEPER and OPENJPA
            }


            commits = GitHubConnector.getCommits(project);
            classes = GitHubConnector.getClasses(project);

            System.out.println(classes);
            IO.appendOnLog("\n\t  FINISHED ANALYZING " + project);
            IO.appendOnLog("******************************************\n");
        }


        IO.appendOnLog("SOFTWARE METRICS ANALYZER SUCCESSFULLY STOPPED\n");
    }
}
