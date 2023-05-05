package control;

import model.ProjectClass;
import model.Releases;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.revwalk.RevCommit;
import utils.IO;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Analyzer {
    public static int computeProportion(List<String> projects) {
        JiraConnector jiraConnector = new JiraConnector();
        return Math.round(jiraConnector.computeProportion(projects)); //Get proportion as cold start for the projects which aren't bookkeeper and openjpa;
    }

    public static void analyzeProjects(List<String> projects, int proportion) {
        JiraConnector jiraConnector = new JiraConnector();

        for (String project : projects) {
            IO.appendOnLog("******************************************");
            IO.appendOnLog("\t  START ANALYZING " + project + "\n");
            try {
                if (Objects.equals(project, "BOOKKEEPER") || Objects.equals(project, "OPENJPA")) {
                    Releases releases = jiraConnector.getInfos(project);;
                    List<RevCommit> commits = GitHubConnector.getCommits(project,releases);
                    String url = "C:\\Users\\simon\\ISW2Projects\\Falessi\\src\\main\\data\\" + project + "\\DataSet.csv";
                    IO.clean(url);
                    for(int i=0;i<releases.getReleaseList().size();i++) {
                        //releases.getReleaseList().get(i).addProjectClass();

                        Map<String, String> versionClasses = GitHubConnector.getClassesForCommit(releases.getReleaseList().get(i).getLastCommit(), project);
                        for (String className:versionClasses.keySet()) {
                            int LOC = 0;
                            /*for(String javaClass : versionClasses.values()) {
                                String[] lines = javaClass.split("\r\n|\r|\n");
                                LOC = ComputeMetrics.computeLOC(lines);
                            }*/

                            releases.getReleaseList().get(i).addProjectClass(new ProjectClass(i,className,versionClasses.values(),LOC));

                            IO.appendOnFile(url,i + "," + className + "," + LOC);
                        }
                    }




                    /*List<RevCommit> commits = GitHubConnector.getCommits(project);
                    List<Release> releases = jiraConnector.getInfos(project,commits).releases;
                    IO.appendOnLog("Computing data set for project: " + project.toLowerCase());
                    GitHubConnector.buildDataSet(project, commits);
                    IO.appendOnLog("Data set successfully saved for project: " + project.toLowerCase());
                */}



            } catch (IOException | GitAPIException e) {
                IO.appendOnLog("ERROR: Error in data split");
            }



            IO.appendOnLog("\n\t  FINISHED ANALYZING " + project);
            IO.appendOnLog("******************************************\n");
        }
    }
}
