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
                    Releases releases = jiraConnector.getInfos(project,"all");
                    IO fileWriter = new IO(project);
                    List<RevCommit> commits = GitHubConnector.getCommits(project);
                    IO.appendOnLog("Start computing software metrics ...");

                    Analyzer.useCommits(releases,commits, proportion, project, fileWriter);

                    for(int i = 0; i < releases.getReleaseList().size(); i++){
                        fileWriter.serializeDataSet(releases.getReleaseList().get(i).getVersionClasses());
                    }

                    IO.appendOnLog("End computing software metrics");

                    List<Releases> newReleases = BugClassDetector.buildWalkForward(project,releases);

                    for(int i = 0;i<newReleases.size() ;i++){
                        Analyzer.useCommits(newReleases.get(i), commits,proportion,project,fileWriter);
                        WalkForward.createFiles(newReleases.get(i),project,releases);
                        IO.appendOnLog("\nComputed training set for release " + i + "\n");
                    }

                    IO.appendOnLog("Walk forward applied successfully");


                    WekaApi.compute(project,releases);

                    IO.appendOnLog("\n\t  FINISHED ANALYZING " + project);
                    IO.appendOnLog("******************************************\n");
                }


            } catch (IOException | GitAPIException e) {
                IO.appendOnLog("ERROR: Error in data split");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static void useCommits(Releases releases, List<RevCommit> commits, int proportion, String project, IO fileWriter) throws IOException {
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



        for(int i = 0; i < releases.getReleaseList().size(); i++){
            GitHubConnector.computeLocForClassInRelease(releases.getReleaseList().get(i));
            for (ProjectClass projectClass: releases.getReleaseList().get(i).getVersionClasses()) {
                GitHubConnector.computeAddedAndDeletedLinesList(projectClass,project);
            }

            ComputeMetrics.computeNR(releases.getReleaseList().get(i).getVersionClasses());
            ComputeMetrics.computeAuthorsNumber(releases.getReleaseList().get(i).getVersionClasses());

            IO.appendOnLog("Retriving classes with bugs ...");
        }


        BugClassDetector.collectClassesWithBug(releases, commits, project,proportion);
    }

}
