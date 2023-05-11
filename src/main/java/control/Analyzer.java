package control;

import model.ProjectClass;
import model.Release;
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
        int roundedProportion = Math.round(jiraConnector.computeProportion(projects));
        IO.appendOnLog("Rounded value of proportion computed with cold start is: " + roundedProportion);
        return roundedProportion; //Get proportion as cold start for the projects which aren't bookkeeper and openjpa;
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
                    List<RevCommit> commits = GitHubConnector.getCommits(project);
                    IO.appendOnLog("Start computing software metrics ...");

                    GitHubConnector.splitCommitsIntoReleases(commits, releases);

                    for (int i = 0; i < releases.getReleaseList().size(); i++) {
                        Map<String, String> versionClasses = GitHubConnector.getClassesForCommit(releases.getReleaseList().get(i).getLastCommit(), project);

                        for (String className : versionClasses.keySet()) {
                            ProjectClass projectClass = new ProjectClass(i, className, versionClasses.get(className));
                            releases.getReleaseList().get(i).addProjectClass(projectClass);
                            GitHubConnector.setFanOut(projectClass);
                            GitHubConnector.setMethodsNumber(projectClass);
                        }

                    }

                    GitHubConnector.computeCommitForClass(releases, fileWriter.getProjectName());  //Associate each commit to a projectClass



                    for(Release release: releases.getReleaseList()){
                        GitHubConnector.computeLocForClassInRelease(release);
                        for (ProjectClass projectClass:release.getVersionClasses()) {
                            GitHubConnector.computeAddedAndDeletedLinesList(projectClass,project);
                        }

                        ComputeMetrics.computeNR(release.getVersionClasses());
                        ComputeMetrics.computeAuthorsNumber(release.getVersionClasses());

                        IO.appendOnLog("Retriving classes with bugs ...");
                    }

                    BugClassDetector.collectClassesWithBug(releases,commits,project,proportion);

                    for(Release release: releases.getReleaseList()){
                        fileWriter.serializeDataSet(release.getVersionClasses());
                    }



                    IO.appendOnLog("End computing software metrics");


                    IO.appendOnLog("\n\t  FINISHED ANALYZING " + project);
                    IO.appendOnLog("******************************************\n");
                }


            } catch (IOException | GitAPIException e) {
                IO.appendOnLog("ERROR: Error in data split");
            }
        }
    }
}
