package control;

import model.ProjectClass;
import model.Release;
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

                        //System.out.println(releases.getReleaseList().get(i).getReleaseClasses()); //Check why no commit for first release
                    }

                    GitHubConnector.computeCommitForClass(releases,commits,fileWriter.getProjectName());  //Associate each commit to a projectClass





                    int i =0;
                    for(Release release: releases.getReleaseList()){
                        System.out.println(i + " " + release.getVersionClasses().size());
                        for (ProjectClass projectClass:release.getVersionClasses()) {
                            GitHubConnector.computeAddedAndDeletedLinesList(projectClass, fileWriter.getProjectName());
                            ComputeMetrics.computeLocAndChurnMetrics(projectClass);
                        }
                        ComputeMetrics.computeNR(release.getVersionClasses());
                        ComputeMetrics.computeAuthorsNumber(release.getVersionClasses());
                        fileWriter.serializeDataSet(release.getVersionClasses());
                        i++;
                    }


                    IO.appendOnLog("\n\t  FINISHED ANALYZING " + project);
                    IO.appendOnLog("******************************************\n");
                }


            } catch (IOException | GitAPIException e) {
                IO.appendOnLog("ERROR: Error in data split");
            }
        }
    }




    private static void computeLocProperties(Releases releases, IO fileWriter) {
        for (Release release: releases.getReleaseList()) {
            int totalCommit =0;
            for (ProjectClass projectClass: release.getVersionClasses()) {
                for (RevCommit commit:projectClass.getCommits()) {
                    //ComputeMetrics.computeLOCMetrics("C:\\Users\\simon\\ISW2Projects\\projects\\" + fileWriter.getProjectName().toLowerCase(), commit, projectClass);
                    totalCommit = totalCommit + 1;
                }
            }
            System.out.println("Number of commits for release: " + totalCommit);
        }
    }
}
