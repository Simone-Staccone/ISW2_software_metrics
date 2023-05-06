package control;

import model.ProjectClass;
import model.Releases;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.revwalk.RevCommit;
import utils.IO;

import java.io.File;
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
            try {
                if (Objects.equals(project, "BOOKKEEPER") || Objects.equals(project, "OPENJPA")) {
                    IO.appendOnLog("******************************************");
                    IO.appendOnLog("\t  START ANALYZING " + project + "\n");
                    Releases releases = jiraConnector.getInfos(project);
                    IO fileWriter = new IO(project);
                    List<RevCommit> commits = GitHubConnector.getCommits(project, releases);
                    for (int i = 0; i < releases.getReleaseList().size(); i++) {
                        Map<String, String> versionClasses = GitHubConnector.getClassesForCommit(releases.getReleaseList().get(i).getLastCommit(), project);

                        for (String className : versionClasses.keySet()) {
                            ProjectClass projectClass = new ProjectClass(i, className, versionClasses.get(className));
                            releases.getReleaseList().get(i).addProjectClass(projectClass);
                        }

                        fileWriter.serializeDataSet(releases.getReleaseList().get(i).getVersionClasses());
                    }

                    Analyzer.computeLocProperties(releases,fileWriter);


                    IO.appendOnLog("\n\t  FINISHED ANALYZING " + project);
                    IO.appendOnLog("******************************************\n");
                }


            } catch (IOException | GitAPIException e) {
                IO.appendOnLog("ERROR: Error in data split");
            }
        }
    }

    private static void computeLocProperties(Releases releases, IO fileWriter) {
        for(int i =1;i<releases.getReleaseList().size();i++){
            ComputeMetrics.computeLOCMetrics(releases.getReleaseList().get(i-1).getLastCommit(),releases.getReleaseList().get(i).getLastCommit(),
                    "C:\\Users\\simon\\ISW2Projects\\projects\\" + fileWriter.getProjectName().toLowerCase() + File.separator);
        }

        //ComputeMetrics.computeLOCMetrics("C:\\Users\\simon\\ISW2Projects\\projects\\" + fileWriter.getProjectName().toLowerCase() + File.separator);
    }
}
