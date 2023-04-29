import control.GitHubConnector;
import control.JiraConnector;
import exceptions.InvalidDataException;
import model.ProjectClass;
import model.Release;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.revwalk.RevCommit;
import org.json.JSONException;
import utils.IO;
import utils.Initializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class Master {
    public static void main(String[] args) throws JSONException {
        Initializer.getInstance();
        IO.appendOnLog("STARTING SOFTWARE METRICS ANALYZER\n");

        JiraConnector jiraConnector = new JiraConnector();
        List<String> projects = Initializer.getProjectNames();
        Iterable<RevCommit> commits;
        List<String> classes;
        List<ProjectClass> projectClasses = new ArrayList<>();

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
                    commits = GitHubConnector.getCommits(project);

                    IO.appendOnLog("Computing project classes for project: " + project.toLowerCase());


                    classes = GitHubConnector.getProjectClassesNames(project);

                    for(String projectClass:classes){
                        projectClasses.add(new ProjectClass(projectClass, "",new Release(1,"",new Date())));
                    }

                    IO.appendOnLog("Obtained project classes for project: " + project.toLowerCase());
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
